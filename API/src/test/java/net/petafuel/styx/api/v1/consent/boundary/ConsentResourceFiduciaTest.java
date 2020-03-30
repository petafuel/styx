package net.petafuel.styx.api.v1.consent.boundary;

import net.petafuel.styx.api.IntegrationTest;
import net.petafuel.styx.api.v1.consent.entity.GetConsentResponse;
import net.petafuel.styx.api.v1.consent.entity.POSTConsentRequest;
import net.petafuel.styx.api.v1.consent.entity.POSTConsentResponse;
import net.petafuel.styx.core.xs2a.entities.AccountAccess;
import net.petafuel.styx.core.xs2a.entities.AccountReference;
import net.petafuel.styx.core.xs2a.entities.Consent;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.runners.MethodSorters;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConsentResourceFiduciaTest extends GetConsentResourceTest {

    private static String consentId;

    @Override
    protected Application configure() {
        ResourceConfig config = setupFiltersAndErrorHandlers();
        return config.register(GetConsentResource.class)
                .register(CreateConsentResource.class);
    }

    @Override
    protected String getBIC() {
        return "GENODEF1M03";
    }

    @Override
    @Test
    @Category(IntegrationTest.class)
    public void A_createConsentTest() throws IOException {
        Invocation.Builder invocationBuilder = target("/v1/consents").request();
        invocationBuilder.header("token", ACCESS_TOKEN);
        invocationBuilder.header("PSU-ID", "PSU-Successful");
        invocationBuilder.header("PSU-BIC", BIC);
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);

        Jsonb jsonb = JsonbBuilder.create();
        AccountReference accountReference = new AccountReference("DE39499999600000005111", AccountReference.Type.IBAN);
        POSTConsentRequest request = new POSTConsentRequest();
        request.setAccess(new AccountAccess());
        request.getAccess().setAccounts(new ArrayList<>());
        request.getAccess().getAccounts().add(accountReference);

        Invocation invocation = invocationBuilder.buildPost(Entity.entity(request, MediaType.APPLICATION_JSON));
        Response response = invocation.invoke(Response.class);
        Assertions.assertEquals(201, response.getStatus());

        POSTConsentResponse consentResponse = jsonb.fromJson(IOUtils.toString((InputStream) response.getEntity(), StandardCharsets.UTF_8), POSTConsentResponse.class);
        Assertions.assertNotNull(consentResponse.getConsentId());
        Assertions.assertNotNull(consentResponse.getAspspScaApproach());
        Assertions.assertNotNull(consentResponse.getLinks());
        consentId = consentResponse.getConsentId();
    }

    @Override
    @Test
    @Category(IntegrationTest.class)
    public void B_fetchConsentTest() throws IOException {
        Assertions.assertNotNull(1);
    }

    @Override
    public void C_getConsentStatusTest() {
        Assertions.assertNotNull(1);
    }
}