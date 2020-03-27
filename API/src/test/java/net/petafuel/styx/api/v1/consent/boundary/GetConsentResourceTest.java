package net.petafuel.styx.api.v1.consent.boundary;

import net.petafuel.styx.api.IntegrationTest;
import net.petafuel.styx.api.StyxRESTTest;
import net.petafuel.styx.api.v1.consent.entity.GetConsentResponse;
import net.petafuel.styx.api.v1.consent.entity.GetConsentStatusResponse;
import net.petafuel.styx.core.xs2a.entities.Consent;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

public abstract class GetConsentResourceTest extends StyxRESTTest {

    protected static final String ACCESS_TOKEN = "d0b10916-7926-4b6c-a90c-3643c62e4b08";

    protected static final String POST_CONSENT = "/v1/consents";
    protected static final String GET_CONSENT = "/v1/consents/%s";
    protected static final String GET_CONSENT_STATUS = "/v1/consents/%s/status";



    protected String BIC = getBIC();

    private Consent consent;
    private GetConsentResponse getConsentResponse;
    private GetConsentStatusResponse getConsentStatusResponse;

    protected abstract String getBIC();

    public Consent getConsent() {
        return consent;
    }

    public void setConsent(Consent consent) {
        this.consent = consent;
    }

    public GetConsentResponse getGetConsentResponse() {
        return getConsentResponse;
    }

    public void setGetConsentResponse(GetConsentResponse getConsentResponse) {
        this.getConsentResponse = getConsentResponse;
    }

    public GetConsentStatusResponse getGetConsentStatusResponse() {
        return getConsentStatusResponse;
    }

    public void setGetConsentStatusResponse(GetConsentStatusResponse getConsentStatusResponse) {
        this.getConsentStatusResponse = getConsentStatusResponse;
    }

    @Test
    @Category(IntegrationTest.class)
    public abstract void A_createConsentTest() throws IOException;

    @Test
    @Category(IntegrationTest.class)
    public abstract void B_fetchConsentTest() throws IOException;

    @Test
    @Category(IntegrationTest.class)
    public abstract void C_getConsentStatusTest();
}
