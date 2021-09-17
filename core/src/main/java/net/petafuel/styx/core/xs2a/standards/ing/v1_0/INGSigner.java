package net.petafuel.styx.core.xs2a.standards.ing.v1_0;

import net.petafuel.styx.core.xs2a.contracts.IXS2AHttpSigner;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.exceptions.SigningException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.BerlinGroupSigner;
import net.petafuel.styx.core.xs2a.standards.ing.v1_0.http.AccessTokenRequest;
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
 * Berlin Group Signer - signs HTTP Request on Application Level
 *
 * @version 1.3
 * @see IXS2AHttpSigner
 */
public final class INGSigner extends BerlinGroupSigner implements IXS2AHttpSigner {

    private static final Logger LOG = LogManager.getLogger(INGSigner.class);

    private static final String SIGNATURE_STRINGFORMAT_ING = "keyId=\"%s\",algorithm=\"%s\",headers=\"%s\",signature=\"%s\"";

    public INGSigner() {
        CertificateManager certificateManager = CertificateManager.getInstance();
        try {
            //QSealC => certificate to use for signing
            X509Certificate sealCrt = certificateManager.getSealCertificate();
            //QWAC certificate
            X509Certificate crt = certificateManager.getCertificate();

            this.sealCertificate = sealCrt.getEncoded();
            this.serialHex = sealCrt.getSerialNumber().toString(16);
            this.issuerDN = sealCrt.getIssuerDN().getName();
            this.algorithm = "rsa-sha256";//custom algorithm for ING

            //create signature with algorithm of styx qwac certificate
            this.signature = Signature.getInstance(crt.getSigAlgName());
            //initialize sign with private key of qseal certificate
            this.signature.initSign(certificateManager.getSealPrivateKey());

        } catch (NoSuchAlgorithmException | InvalidKeyException | CertificateException e) {
            LOG.error(e.getMessage());
            throw new SigningException(e.getMessage(), e);
        }
    }

    @Override
    public void sign(XS2ARequest xs2aRequest) {
        // Digest message body first so we can use this header for signing

        try {
            this.digest(xs2aRequest);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Unable to digest message: {}", e.getMessage());
        }

        Map<String, String> headers = xs2aRequest.getHeaders();

        //Sandbox specials
        //todo delete for commit
        headers.remove(XS2AHeader.TPP_REDIRECT_URL);
        headers.put(XS2AHeader.TPP_REDIRECT_URL, "https://example.com/redirect");
        headers.remove(XS2AHeader.PSU_IP_ADDRESS);
        headers.put(XS2AHeader.PSU_IP_ADDRESS, "37.228.130.0");

        StringJoiner signatureStructureJoiner = new StringJoiner(" ");
        StringJoiner signatureContentJoiner = new StringJoiner("\n");
        LinkedList<String> supportedHeaders = new LinkedList<>();
        supportedHeaders.add(XS2AHeader.DIGEST);
        supportedHeaders.add(XS2AHeader.X_REQUEST_ID);
        supportedHeaders.add(XS2AHeader.DATE);
        supportedHeaders.add(XS2AHeader.PSU_ID);
        supportedHeaders.add(XS2AHeader.PSU_CORPORATE_ID);
        supportedHeaders.add(XS2AHeader.TPP_REDIRECT_URL);
        //additional header for signing
        supportedHeaders.add(XS2AHeader.REQUEST_TARGET);

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
            LOG.error("Unable to update signature: {}", e.getMessage());
        }
        String singedHeaders = null;
        try {
            singedHeaders = Base64.getEncoder().encodeToString(this.signature.sign());
        } catch (SignatureException e) {
            LOG.error(e.getStackTrace());
        }

        if (headers.containsKey("client_id")) {
            //set client_id as key for all calls after getting ing access token and add signature header
            xs2aRequest.addHeader(XS2AHeader.SIGNATURE, String.format(SIGNATURE_STRINGFORMAT_ING,
                    headers.get("client_id"),
                    this.algorithm,
                    headerOrder,
                    singedHeaders));
        } else {
            //to get ing access token set signature key to serialHex and issuerDn and authorization header
            xs2aRequest.addHeader(XS2AHeader.AUTHORIZATION, "Signature " + String.format(SIGNATURE_STRINGFORMAT,
                    this.serialHex,
                    this.issuerDN,
                    this.algorithm,
                    headerOrder,
                    singedHeaders));
            //add tpp signature certificate only for oauth/token call
            this.addCertificate(xs2aRequest);
        }
        //remove added headers after signing => header were just added for signing and should not be part of the real request
        xs2aRequest.removeHeader(XS2AHeader.REQUEST_TARGET);
        xs2aRequest.removeHeader(XS2AHeader.ING_CLIENT_ID);
    }

    /**
     * Digest the message body and add to request
     *
     * @param request XS2ARequest
     * @throws NoSuchAlgorithmException Throws the exception in case the SHA-512 Algorithm is not supported
     */
    @SuppressWarnings("squid:S4790")
    @Override
    protected void digest(XS2ARequest request) throws NoSuchAlgorithmException {
        //SHA-512 is predefined by the berlingroup spec for generating the http body digest RFC5843
        //but ing needs SHA-256 for digest of accessToken Call
        String algorithm = "SHA-512";
        if (request.getClass() == AccessTokenRequest.class) {
            algorithm = "SHA-256";
        }
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        byte[] requestBodyBytes = request.getRawBody().orElse("").getBytes(StandardCharsets.UTF_8);
        byte[] digestHeader = messageDigest.digest(requestBodyBytes);
        request.addHeader(XS2AHeader.DIGEST, algorithm + "=" + Base64.getEncoder().encodeToString(digestHeader));
    }

    /**
     * add certificate to request as base64 String
     *
     * @param request The full XS2ARequest that should contain the certificate
     */
    @Override
    protected void addCertificate(XS2ARequest request) {
        request.addHeader(XS2AHeader.TPP_SIGNATURE_CERTIFICATE, Base64.getEncoder().encodeToString(this.sealCertificate));
    }
}
