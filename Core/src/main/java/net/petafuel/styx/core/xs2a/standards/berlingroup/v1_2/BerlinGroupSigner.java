package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2;

import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.exceptions.SigningException;
import net.petafuel.styx.core.xs2a.contracts.IBerlinGroupSigner;
import net.petafuel.styx.core.xs2a.utils.CertificateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Berlin Group Signer to sign HTTP Requests on an Application Layer
 * @version 1.2
 * @see IBerlinGroupSigner
 */
public class BerlinGroupSigner implements IBerlinGroupSigner {
    private static final Logger LOG = LogManager.getLogger(BerlinGroupSigner.class);

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
            LOG.error("Unable to digest message: " + e.getMessage());
        }

        LinkedHashMap<String, String> headers = xs2aRequest.getHeaders();

        StringJoiner signatureStructureJoiner = new StringJoiner(" ");
        StringJoiner signatureContentJoiner = new StringJoiner("\n");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            switch (entry.getKey()) {
                case XS2AHeader.DIGEST:
                    signatureStructureJoiner.add(XS2AHeader.DIGEST);
                    signatureContentJoiner.add(XS2AHeader.DIGEST + ": " + entry.getValue());
                    break;
                case XS2AHeader.X_REQUEST_ID:
                    signatureStructureJoiner.add(XS2AHeader.X_REQUEST_ID);
                    signatureContentJoiner.add(XS2AHeader.X_REQUEST_ID + ": " + entry.getValue());
                    break;
                case XS2AHeader.DATE:
                    signatureStructureJoiner.add(XS2AHeader.DATE);
                    signatureContentJoiner.add(XS2AHeader.DATE + ": " + entry.getValue());
                    break;
                case XS2AHeader.PSU_ID:
                    signatureStructureJoiner.add(XS2AHeader.PSU_ID);
                    signatureContentJoiner.add(XS2AHeader.PSU_ID + ": " + entry.getValue());
                    break;
                case XS2AHeader.PSU_CORPORATE_ID:
                    signatureStructureJoiner.add(XS2AHeader.PSU_CORPORATE_ID);
                    signatureContentJoiner.add(XS2AHeader.PSU_CORPORATE_ID + ": " + entry.getValue());
                    break;
                case XS2AHeader.TPP_REDIRECT_URL:
                    signatureStructureJoiner.add(XS2AHeader.TPP_REDIRECT_URL);
                    signatureContentJoiner.add(XS2AHeader.TPP_REDIRECT_URL + ": " + entry.getValue());
                    break;
                default:
                    //can't handle unknown headers
                    break;
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

        xs2aRequest.setHeader(XS2AHeader.SIGNATURE, String.format(SIGNATURE_STRINGFORMAT,
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
     * @throws NoSuchAlgorithmException Throws the exception in case the SHA-256 Algorithm is not supported
     */
    private void digest(XS2ARequest request) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] digestHeader = messageDigest.digest(request.getRawBody().getBytes(StandardCharsets.UTF_8));
        request.setHeader(XS2AHeader.DIGEST, "SHA-256=" + Base64.getEncoder().encodeToString(digestHeader));
    }

    /**
     * add certificate to request as base64 String
     *
     * @param request The full XS2ARequest that should contain the certificate
     */
    private void addCertificate(XS2ARequest request) {
        request.setHeader(XS2AHeader.TPP_SIGNATURE_CERTIFICATE, Base64.getEncoder().encodeToString(this.certificate));
    }
}
