package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2;

import net.petafuel.styx.core.xs2a.contracts.IXS2AHttpSigner;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.exceptions.SigningException;
import net.petafuel.styx.core.xs2a.utils.CertificateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.LinkedList;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Berlin Group Signer to sign HTTP Requests on an Application Layer
 *
 * @version 1.2
 * @see IXS2AHttpSigner
 */
public class BerlinGroupSigner implements IXS2AHttpSigner {
    private static final Logger LOG = LogManager.getLogger(BerlinGroupSigner.class);

    /**
     * $1 certificate serial
     * $2 certificate issuer DN
     * $3 algorithm used by the certificate signature
     * $4 headers to be included in signature, order must be kept as in the raw HTTP Request
     * $5 Signature from headerlines defined in $4, hashed with $3 and base64 encoded
     */
    private static final String SIGNATURE_STRINGFORMAT = "keyId=\"SN=%s,CA=%s\",algorithm=\"%s\",headers=\"%s\",signature=\"%s\"";

    private final Signature signature;
    private final byte[] certificate;
    private final String serialHex;
    private final String issuerDN;
    private final String algorithm;

    public BerlinGroupSigner() {
        CertificateManager certificateManager = CertificateManager.getInstance();
        try {

            X509Certificate crt = certificateManager.getCertificate();
            this.certificate = crt.getEncoded();
            this.serialHex = crt.getSerialNumber().toString(16);
            this.issuerDN = crt.getIssuerDN().getName();
            this.algorithm = crt.getSigAlgName();

            this.signature = Signature.getInstance(this.algorithm);
            this.signature.initSign(certificateManager.getPrivateKey());

        } catch (NoSuchAlgorithmException | InvalidKeyException | CertificateException e) {
            LOG.error(e.getMessage());
            throw new SigningException(e.getMessage(), e);
        }
    }

    public void sign(XS2ARequest xs2aRequest) {
        // Digest message body first so we can use this header for signing

        try {
            this.digest(xs2aRequest);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Unable to digest message: {}", e.getMessage());
        }

        Map<String, String> headers = xs2aRequest.getHeaders();

        StringJoiner signatureStructureJoiner = new StringJoiner(" ");
        StringJoiner signatureContentJoiner = new StringJoiner("\n");
        LinkedList<String> supportedHeaders = new LinkedList<>();
        supportedHeaders.add(XS2AHeader.DIGEST);
        supportedHeaders.add(XS2AHeader.X_REQUEST_ID);
        supportedHeaders.add(XS2AHeader.DATE);
        supportedHeaders.add(XS2AHeader.PSU_ID);
        supportedHeaders.add(XS2AHeader.PSU_CORPORATE_ID);
        supportedHeaders.add(XS2AHeader.TPP_REDIRECT_URL);

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (supportedHeaders.contains(key)) {
                signatureStructureJoiner.add(key);
                signatureContentJoiner.add(key + ": " + value);
            }
        }
        String headerOrder = signatureStructureJoiner.toString();
        String signatureContent = signatureContentJoiner.toString();

        try {
            this.signature.update(signatureContent.getBytes(StandardCharsets.UTF_8));
        } catch (SignatureException e) {
            LOG.error(e.getStackTrace());
        }
        String singedHeaders = null;
        try {
            singedHeaders = Base64.getEncoder().encodeToString(this.signature.sign());
        } catch (SignatureException e) {
            LOG.error(e.getStackTrace());
        }

        xs2aRequest.addHeader(XS2AHeader.SIGNATURE, String.format(SIGNATURE_STRINGFORMAT,
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
     * @throws NoSuchAlgorithmException Throws the exception in case the SHA-512 Algorithm is not supported
     */
    //SHA-512 is predefined by the berlingroup spec for generating the http body digest RFC5843
    @SuppressWarnings("squid:S4790")
    private void digest(XS2ARequest request) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        byte[] requestBodyBytes = request.getRawBody().orElse("").getBytes(StandardCharsets.UTF_8);
        byte[] digestHeader = messageDigest.digest(requestBodyBytes);
        request.addHeader(XS2AHeader.DIGEST, "SHA-512=" + Base64.getEncoder().encodeToString(digestHeader));
    }

    /**
     * add certificate to request as base64 String
     *
     * @param request The full XS2ARequest that should contain the certificate
     */
    private void addCertificate(XS2ARequest request) {
        request.addHeader(XS2AHeader.TPP_SIGNATURE_CERTIFICATE, Base64.getEncoder().encodeToString(this.certificate));
    }
}
