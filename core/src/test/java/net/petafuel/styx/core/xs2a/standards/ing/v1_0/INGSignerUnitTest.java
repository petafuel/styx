package net.petafuel.styx.core.xs2a.standards.ing.v1_0;

import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.utils.CertificateManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Optional;

class INGSignerUnitTest {

    private XS2ARequest xs2ARequest;
    private INGSigner ingSigner;

    @BeforeEach
    void setUp() {
        ingSigner = new INGSigner();
        xs2ARequest = new XS2ARequest() {
            @Override
            public Optional<String> getRawBody() {
                return Optional.empty();
            }

            @Override
            public BasicService.RequestType getHttpMethod() {
                return BasicService.RequestType.GET;
            }

            @Override
            public String getServicePath() {
                return null;
            }
        };
    }

    @Test
    void testSignWithClientId() {
        xs2ARequest.addHeader(INGSigner.ING_CLIENT_ID, "test");
        xs2ARequest.addHeader(INGSigner.REQUEST_TARGET, "post /v1/payments/sepa-credit-transfers");
        ingSigner.sign(xs2ARequest);

        //assert headers
        Assertions.assertFalse(xs2ARequest.getHeaders().containsKey(INGSigner.ING_CLIENT_ID));
        Assertions.assertNotNull(xs2ARequest.getHeaders().get(XS2AHeader.SIGNATURE));

        //assert given keyId is set and algorithm and request-target headers for ing
        String signature = xs2ARequest.getHeaders().get(XS2AHeader.SIGNATURE);
        Assertions.assertTrue(signature.contains("keyId=\"test\""));
        Assertions.assertTrue(signature.contains("algorithm=\"rsa-sha256\""));
        Assertions.assertTrue(signature.contains("headers=\""+INGSigner.REQUEST_TARGET));
    }

    @Test
    void testSignWithoutClientId() throws CertificateEncodingException {
        xs2ARequest.addHeader(INGSigner.REQUEST_TARGET, "post /v1/oauth2/token");
        ingSigner.sign(xs2ARequest);

        //assert headers
        Assertions.assertFalse(xs2ARequest.getHeaders().containsKey(INGSigner.ING_CLIENT_ID));
        Assertions.assertNull(xs2ARequest.getHeaders().get(XS2AHeader.SIGNATURE));
        Assertions.assertNotNull(xs2ARequest.getHeaders().get(XS2AHeader.AUTHORIZATION));

        //assert algorithm and request-target headers for ing
        String authorization = xs2ARequest.getHeaders().get(XS2AHeader.AUTHORIZATION);
        Assertions.assertTrue(authorization.contains("algorithm=\"rsa-sha256\""));
        Assertions.assertTrue(authorization.contains("headers=\""+INGSigner.REQUEST_TARGET));

        //assert keyId is build from qseal certificate
        CertificateManager certificateManager = CertificateManager.getInstance();
        X509Certificate sealCrt = certificateManager.getSealCertificate();
        String serialHex = sealCrt.getSerialNumber().toString(16);
        String issuerDN = sealCrt.getIssuerDN().getName();
        Assertions.assertTrue(authorization.contains("Signature keyId=\"SN=" + serialHex + ",CA=" + issuerDN + "\""));
    }
}
