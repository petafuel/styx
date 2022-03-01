package net.petafuel.styx.api.ioprocessing.options;

import net.petafuel.styx.api.IntegrationTest;
import net.petafuel.styx.api.StyxRESTTest;
import net.petafuel.styx.api.v1.authentication.boundary.AuthenticationResource;
import net.petafuel.styx.api.v1.payment.boundary.PaymentInitiationResource;
import net.petafuel.styx.core.banklookup.sad.entities.Aspsp;
import net.petafuel.styx.core.banklookup.sad.entities.Config;
import net.petafuel.styx.core.banklookup.sad.entities.ImplementerOption;
import net.petafuel.styx.core.banklookup.sad.entities.Url;
import net.petafuel.styx.core.ioprocessing.IOParser;
import net.petafuel.styx.core.ioprocessing.IOOrder;
import net.petafuel.styx.core.ioprocessing.ImplementerOptionException;
import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.standards.ing.v1_0.INGSigner;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Assume;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Assertions;
import javax.ws.rs.core.Application;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

public class STYX09IntegrationTest extends StyxRESTTest {

    private static final String TEST_BIC = "INGDDEFF";
    private static Aspsp ing;
    private static ImplementerOption styx09Option;
    private static final String ingClientId = "5ca1ab1e-c0ca-c01a-cafe-154deadbea75";

    @Override
    protected Application configure() {
        styx09Option = new ImplementerOption();
        styx09Option.setId("STYX09");
        ing = new Aspsp();
        ing.setConfig(new Config());
        ing.setBic(TEST_BIC);
        Url url = new Url();
        url.setCommonUrl("https://api.sandbox.ing.com");
        ing.setSandboxUrl(url);

        ResourceConfig config = setupFiltersAndErrorHandlers();
        if (pisAccessToken == null || Objects.equals(pisAccessToken, "")) {
            Assertions.fail("test.token.access.pis not set in test properties");
        }
        return config.register(AuthenticationResource.class).register(PaymentInitiationResource.class);
    }

    @Test
    @Category(IntegrationTest.class)
    public void testStyx09() throws ImplementerOptionException {
        Assume.assumeNotNull(ing);
        Assume.assumeNotNull(styx09Option);

        styx09Option.setOptions(Collections.singletonMap("required", true));
        ing.getConfig().setImplementerOptions(Collections.singletonMap("STYX09", styx09Option));
        IOParser ioParser = new IOParser(ing);

        STYX09 styx09 = new STYX09(ioParser);

        Assertions.assertEquals(IOOrder.POST_CREATION, styx09.order());

        XS2ARequest xs2ARequest = new XS2ARequest() {
            @Override
            public Optional<String> getRawBody() {
                return Optional.empty();
            }

            @Override
            public BasicService.RequestType getHttpMethod() {
                return BasicService.RequestType.POST;
            }

            @Override
            public String getServicePath() {
                return "";
            }
        };

        Assertions.assertTrue(styx09.apply(null, xs2ARequest, null));
        Assertions.assertNotNull(xs2ARequest.getHeaders().get(XS2AHeader.AUTHORIZATION));
        Assertions.assertNotNull(xs2ARequest.getHeaders().get(INGSigner.ING_CLIENT_ID));
        Assertions.assertNotNull(xs2ARequest.getHeaders().get(INGSigner.REQUEST_TARGET));
        Assertions.assertEquals(ingClientId, xs2ARequest.getHeaders().get(INGSigner.ING_CLIENT_ID));
        Assertions.assertNotEquals("post /oauth2/token", xs2ARequest.getHeaders().get(INGSigner.REQUEST_TARGET));
    }
}
