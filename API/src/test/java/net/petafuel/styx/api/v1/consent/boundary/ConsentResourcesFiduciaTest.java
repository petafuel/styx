package net.petafuel.styx.api.v1.consent.boundary;

import net.petafuel.styx.api.IntegrationTest;
import net.petafuel.styx.api.v1.consent.entity.GetConsentResponse;
import net.petafuel.styx.api.v1.consent.entity.GetConsentStatusResponse;
import net.petafuel.styx.api.v1.consent.entity.POSTConsentResponse;
import net.petafuel.styx.core.xs2a.entities.AccountReference;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.SCA;
import org.apache.commons.io.IOUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.runners.MethodSorters;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConsentResourcesFiduciaTest extends ConsentResourcesTest {

    @Override
    protected String getBIC() {
        return "GENODEF1M03";
    }

    @Override
    protected String getPsuId(){
        return "VRK1234567890ALL";
    }

    @Override
    protected String getPsuIpAddress() {
        return "192.168.8.78";
    }

    @Override
    protected String getPsuPassword() {
        return "password";
    }

    @Override
    protected String getSCAMethodId() {
        return "942";
    }

    @Override
    protected String getPsuOtp() {
        return "123456";
    }

    @Override
    protected AccountReference getAccountReference(){
        return new AccountReference("DE39499999600000005111", AccountReference.Type.IBAN);
    }

    @Override
    @Test
    @Category(IntegrationTest.class)
    public void A_createConsentTest() throws IOException {
        Response response = createConsentEndpoint();
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
        Response response = fetchConsentEndpoint();

        Assertions.assertEquals(200, response.getStatus());
        GetConsentResponse consentResponse = jsonb.fromJson(IOUtils.toString((InputStream) response.getEntity(), StandardCharsets.UTF_8), GetConsentResponse.class);
        Assertions.assertNotNull(consentResponse.getConsent().getId());
        Assertions.assertTrue(consentResponse.getConsent().getAccess().getAccounts().contains(getAccountReference()));
    }

    @Override
    @Test
    @Category(IntegrationTest.class)
    public void C_getConsentStatusTest() throws IOException {
        Response response = getConsentStatusEndpoint();

        Assertions.assertEquals(200, response.getStatus());
        GetConsentStatusResponse consentStatusResponse = jsonb.fromJson(IOUtils.toString((InputStream) response.getEntity(), StandardCharsets.UTF_8), GetConsentStatusResponse.class);
        Assertions.assertEquals(Consent.State.RECEIVED, consentStatusResponse.getState());
    }

    @Test
    @Category(IntegrationTest.class)
    public void D_startConsentAuthorisationTest(){
        SCA response = startConsentAuthorisationEndpoint();
        Assertions.assertEquals(SCA.Status.PSUAUTHENTICATED, response.getScaStatus());
        Assertions.assertEquals(SCA.Approach.EMBEDDED, response.getApproach());
        authorisationId = response.getAuthorisationId();
    }

    @Test
    @Category(IntegrationTest.class)
    public void F_selectSCAMethod() throws IOException {
        SCA response = selectSCAMethodEndpoint();
        Assertions.assertEquals(SCA.Status.STARTED, response.getScaStatus());
    }

    @Test
    @Category(IntegrationTest.class)
    public void G_authoriseTransactionWithTANOTP() throws IOException {
        SCA response = authoriseTransactionWithTANOTPEndpoint();
        Assertions.assertEquals(SCA.Status.FINALISED, response.getScaStatus());
    }
}