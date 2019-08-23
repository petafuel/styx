package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2;

import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.exceptions.SigningException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

public class BerlinGroupSigner {
    private static final Logger LOG = LogManager.getLogger(BerlinGroupSigner.class);
    /**
     * RFC 7230 (HTTP/1.1) -> Header Fields are case-INsensitive
     * RFC 7540 (HTTP/2) -> Header Fields remain case-INsensitive
     */
    private static final String HEADER_TPP_SIGNATURE_CERTIFICATE = "tpp-signature-certificate";
    private static final String HEADER_DIGEST = "digest";
    private static final String HEADER_X_REQUEST_ID = "x-request-id";
    private static final String HEADER_DATE = "date";
    private static final String HEADER_PSU_ID = "psu-id";
    private static final String HEADER_PSU_CORPORATE_ID = "psu-corporate-id";
    private static final String HEADER_TPP_REDIRECT_URL = "tpp-redirect-uri";
    private static final String HEADER_SIGNATURE = "signature";
    /**
     * $1 certificate serial
     * $2 certificate issuer DN
     * $3 algorithm used by the certificate signature
     * $4 headers to be included in signature, order must be kept as in the raw HTTP Request
     * $5 Signature from headerlines defined in $4, hashed with $3 and base64 encoded
     */
    private static final String SIGNATURE_STRINGFORMAT = "keyId=\"SN=%s,CA=%s\",algorithm=\"%s\",headers=\"%s\",signature=\"%s\"";

    private Signature signature;
    private byte[] certificate;
    private String serialHex;
    private String issuerDN;
    private String algorithm;

    public BerlinGroupSigner() {
        Properties config = new Properties();
        try (InputStream in = BerlinGroupSigner.class.getClassLoader().getResourceAsStream("config.properties");) {
            config.load(in);
        } catch (IOException e) {
            LOG.error("Error while loading properties: " + e.getMessage());
        }
        String p12Path = config.getProperty("certificate.path");
        String passphraseFilePath = config.getProperty("certificate.passphrasefile.path");

        try {
            char[] passphrase = new String(Files.readAllBytes(Paths.get(passphraseFilePath)), StandardCharsets.UTF_8).toCharArray();
            KeyStore pkcs12Bundle = KeyStore.getInstance("pkcs12");
            try (FileInputStream p12IS = new FileInputStream(p12Path)) {
                pkcs12Bundle.load(p12IS, passphrase);
                this.signature = Signature.getInstance("SHA256withRSA");
                if (pkcs12Bundle.aliases().hasMoreElements()) {
                    String crtName = pkcs12Bundle.aliases().nextElement();
                    this.signature.initSign((PrivateKey) pkcs12Bundle.getKey(crtName, passphrase));

                    X509Certificate crt = (X509Certificate) pkcs12Bundle.getCertificate(crtName);

                    this.certificate = crt.getEncoded();
                    this.serialHex = crt.getSerialNumber().toString(16);
                    this.issuerDN = crt.getIssuerDN().getName();
                    this.algorithm = crt.getSigAlgName();
                } else {
                    throw new SecurityException("Unable to find correct certificate within p12 bundle");
                }
            }
        } catch (FileNotFoundException e) {
            LOG.error("Unable to open file" + e.getMessage());
            throw new SigningException(e.getMessage());
        } catch (NoSuchAlgorithmException | IOException | UnrecoverableKeyException | InvalidKeyException | CertificateException e) {
            LOG.error(e.getMessage());
            throw new SigningException(e.getMessage());
        } catch (KeyStoreException e) {
            LOG.error("Unable to extract data from Certificate: " + e.getMessage());
            throw new SigningException(e.getMessage());
        }
    }

    public void sign(XS2ARequest xs2aRequest) {
        // Digest message body first so we can use this header for signing

        try {
            this.digest(xs2aRequest);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Unable to digest message: " + e.getMessage());
        }

        LinkedHashMap<String, String> headers = xs2aRequest.getHeaders();

        StringJoiner signatureStructureJoiner = new StringJoiner(" ");
        StringJoiner signatureContentJoiner = new StringJoiner("\n");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            switch (entry.getKey()) {
                case HEADER_DIGEST:
                    signatureStructureJoiner.add(HEADER_DIGEST);
                    signatureContentJoiner.add(HEADER_DIGEST + ": " + entry.getValue());
                    break;
                case HEADER_X_REQUEST_ID:
                    signatureStructureJoiner.add(HEADER_X_REQUEST_ID);
                    signatureContentJoiner.add(HEADER_X_REQUEST_ID + ": " + entry.getValue());
                    break;
                case HEADER_DATE:
                    signatureStructureJoiner.add(HEADER_DATE);
                    signatureContentJoiner.add(HEADER_DATE + ": " + entry.getValue());
                    break;
                case HEADER_PSU_ID:
                    signatureStructureJoiner.add(HEADER_PSU_ID);
                    signatureContentJoiner.add(HEADER_PSU_ID + ": " + entry.getValue());
                    break;
                case HEADER_PSU_CORPORATE_ID:
                    signatureStructureJoiner.add(HEADER_PSU_CORPORATE_ID);
                    signatureContentJoiner.add(HEADER_PSU_CORPORATE_ID + ": " + entry.getValue());
                    break;
                case HEADER_TPP_REDIRECT_URL:
                    signatureStructureJoiner.add(HEADER_TPP_REDIRECT_URL);
                    signatureContentJoiner.add(HEADER_TPP_REDIRECT_URL + ": " + entry.getValue());
                    break;
            }
        }
        String headerOrder = signatureStructureJoiner.toString();
        String signatureContent = signatureContentJoiner.toString();

        try {
            this.signature.update(signatureContent.getBytes(StandardCharsets.UTF_8));
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        String singedHeaders = null;
        try {
            singedHeaders = Base64.getEncoder().encodeToString(this.signature.sign());
        } catch (SignatureException e) {
            e.printStackTrace();
        }

        xs2aRequest.setHeader(HEADER_SIGNATURE, String.format(SIGNATURE_STRINGFORMAT,
                this.serialHex,
                this.issuerDN,
                this.algorithm,
                headerOrder,
                singedHeaders));
        this.addCertificate(xs2aRequest);
    }

    /**
     * Digest the message body and add to request
     *
     * @param request XS2ARequest
     * @throws NoSuchAlgorithmException
     */
    private void digest(XS2ARequest request) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] digestHeader = messageDigest.digest(request.getRawBody().getBytes(StandardCharsets.UTF_8));
        request.setHeader(HEADER_DIGEST, "SHA-256=" + Base64.getEncoder().encodeToString(digestHeader));
    }

    /**
     * add certificate to request
     *
     * @param request
     */
    private void addCertificate(XS2ARequest request) {
        request.setHeader(HEADER_TPP_SIGNATURE_CERTIFICATE, Base64.getEncoder().encodeToString(this.certificate));
    }
}
