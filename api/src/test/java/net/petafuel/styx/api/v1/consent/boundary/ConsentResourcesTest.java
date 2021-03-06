package net.petafuel.styx.api.v1.consent.boundary;

import net.petafuel.styx.api.StyxRESTTest;
import net.petafuel.styx.api.v1.consent.entity.POSTConsentRequest;
import net.petafuel.styx.api.v1.payment.entity.AuthorisationRequest;
import net.petafuel.styx.api.v1.payment.entity.AuthorisationStatusResponse;
import net.petafuel.styx.core.xs2a.entities.AccountAccess;
import net.petafuel.styx.core.xs2a.entities.AccountReference;
import net.petafuel.styx.core.xs2a.entities.PSUData;
import net.petafuel.styx.core.xs2a.entities.SCA;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.FixMethodOrder;
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
import java.util.ArrayList;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class ConsentResourcesTest extends StyxRESTTest {
    private static final String POST_CONSENT = "/v1/consents";
    private static final String GET_CONSENT = "/v1/consents/%s";
    private static final String GET_CONSENT_STATUS = "/v1/consents/%s/status";
    private static final String POST_CONSENT_AUTHORISATION = "/v1/consents/%s/authorisations";
    private static final String UPDATE_CONSENT_AUTHORISATION = "/v1/consents/%s/authorisations/%s";

    protected Jsonb jsonb = JsonbBuilder.create();

    static public String consentId;
    static String authorisationId;

    protected abstract String getBIC();
    protected abstract String getPsuId();
    protected abstract String getPsuIpAddress();
    protected abstract String getPsuPassword();
    protected abstract String getSCAMethodId();
    protected abstract String getPsuOtp();
    protected abstract AccountReference getAccountReference();

    public abstract void A_createConsentTest() throws IOException;

    public abstract void B_fetchConsentTest() throws IOException;

    public abstract void C_getConsentStatusTest() throws IOException;

    @Override
    protected Application configure() {
        ResourceConfig config = setupFiltersAndErrorHandlers();
        return config.register(GetConsentResource.class)
                .register(CreateConsentResource.class)
                .register(ConsentAuthorisationResource.class);
    }

    Response createConsentEndpoint() {

        Invocation.Builder invocationBuilder = getInvocationBuilder(String.format(POST_CONSENT, consentId));

        POSTConsentRequest request = new POSTConsentRequest();
        request.setAccess(new AccountAccess());
        request.getAccess().setAccounts(new ArrayList<>());
        request.getAccess().getAccounts().add(getAccountReference());
        request.getAccess().setBalances(new ArrayList<>());
        request.getAccess().getBalances().add(getAccountReference());
        request.getAccess().setTransactions(new ArrayList<>());
        request.getAccess().getTransactions().add(getAccountReference());
        request.setRecurringIndicator(true);
        Invocation invocation = invocationBuilder.buildPost(Entity.entity(request, MediaType.APPLICATION_JSON));
        return invocation.invoke(Response.class);
    }

    Response fetchConsentEndpoint() {

        Invocation.Builder invocationBuilder = getInvocationBuilder(String.format(GET_CONSENT, consentId));
        Invocation invocation = invocationBuilder.buildGet();
        return invocation.invoke(Response.class);
    }

    Response getConsentStatusEndpoint() {

        Invocation.Builder invocationBuilder = getInvocationBuilder(String.format(GET_CONSENT_STATUS, consentId));
        Invocation invocation = invocationBuilder.buildGet();
        return invocation.invoke(Response.class);
    }

    SCA startConsentAuthorisationEndpoint() {
        Invocation.Builder invocationBuilder = getInvocationBuilder(String.format(POST_CONSENT_AUTHORISATION, consentId));
        PSUData psuData = new PSUData();
        psuData.setPassword(getPsuPassword());
        AuthorisationRequest authorisationRequest = new AuthorisationRequest();
        authorisationRequest.setPsuData(psuData);
        Invocation invocation = invocationBuilder.buildPost(Entity.entity(authorisationRequest, MediaType.APPLICATION_JSON));
        return invocation.invoke(SCA.class);
    }

    SCA selectSCAMethodEndpoint() {
        Invocation.Builder invocationBuilder = getInvocationBuilder(String.format(UPDATE_CONSENT_AUTHORISATION, consentId, authorisationId));
        AuthorisationRequest authorisationRequest = new AuthorisationRequest();
        authorisationRequest.setAuthenticationMethodId(getSCAMethodId());
        Invocation invocation = invocationBuilder.buildPut(Entity.entity(authorisationRequest, MediaType.APPLICATION_JSON));
        return invocation.invoke(SCA.class);
    }

    SCA authoriseTransactionWithTANOTPEndpoint() {
        Invocation.Builder invocationBuilder = getInvocationBuilder(String.format(UPDATE_CONSENT_AUTHORISATION, consentId, authorisationId));
        AuthorisationRequest authorisationRequest = new AuthorisationRequest();
        authorisationRequest.setScaAuthenticationData(getPsuOtp());
        Invocation invocation = invocationBuilder.buildPut(Entity.entity(authorisationRequest, MediaType.APPLICATION_JSON));
        return invocation.invoke(SCA.class);
    }

    private Invocation.Builder getInvocationBuilder(String target) {

        Invocation.Builder invocationBuilder = target(target).request();
        invocationBuilder.header("token", aisAccessToken);
        invocationBuilder.header("PSU-ID", getPsuId());
        invocationBuilder.header("PSU-BIC", getBIC());
        invocationBuilder.header("PSU-IP-Address", getPsuIpAddress());
        invocationBuilder.header("redirectPreferred", true);

        if (this.getClass().getSimpleName().toLowerCase().contains("targo")) {
            invocationBuilder.header("X-STYX-X-bvpsd2-test-apikey", "tUfZ5KOHRTFrikZUsmSMUabKw09UIzGE");
        }
        return invocationBuilder;
    }

    AuthorisationStatusResponse getStatusAuthorisation() {
        Invocation.Builder invocationBuilder = getInvocationBuilder(String.format(UPDATE_CONSENT_AUTHORISATION, consentId, authorisationId));
            Invocation invocation = invocationBuilder.buildGet();
            return invocation.invoke(AuthorisationStatusResponse .class);
    }
}
