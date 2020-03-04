package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.persistence.layers.PersistentConsent;
import net.petafuel.styx.core.xs2a.contracts.BasicAuthorisationService;
import net.petafuel.styx.core.xs2a.contracts.CSInterface;
import net.petafuel.styx.core.xs2a.contracts.IXS2AHttpSigner;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.sca.SCAUtils;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.CreateConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.serializers.AccountSerializer;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.serializers.ConsentSerializer;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.serializers.ConsentStatusSerializer;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.ConsentUpdatePSUDataRequest;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class BerlinGroupCS extends BasicAuthorisationService implements CSInterface {

    private static final Logger LOG = LogManager.getLogger(BerlinGroupCS.class);

    private static final String POST_CONSENT = "/v1/consents";
    private static final String GET_CONSENT = "/v1/consents/%s";
    private static final String GET_CONSENT_STATUS = "/v1/consents/%s/status";
    private static final String DELETE_CONSENT = "/v1/consents/%s";
    private static final String UPDATE_PSU_DATA = "/v1/consents/%s/authorisations/%s";

    public BerlinGroupCS(String url, IXS2AHttpSigner signer) {
        super(LOG, url, signer);
    }

    public Consent createConsent(XS2ARequest consentRequest) throws BankRequestFailedException {
        this.setUrl(this.url + POST_CONSENT);
        this.createBody(RequestType.POST, JSON, consentRequest);
        this.createHeaders(consentRequest);

        try (Response response = this.execute()) {
            String responseBody = extractResponseBody(response, 201);

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Consent.class, new ConsentSerializer())
                    .create();
            Consent consent = gson.fromJson(responseBody, Consent.class);
            consent.setxRequestId(UUID.fromString(consentRequest.getHeaders().get("x-request-id")));
            consent.setPsu(((CreateConsentRequest) consentRequest).getConsent().getPsu());
            //if the sca method was not set by previously parsing the body, use the bank supplied header
            SCAUtils.parseSCAApproach(consent.getSca(), response);
            consent.setAccess(((CreateConsentRequest) consentRequest).getConsent().getAccess());
            new PersistentConsent().create(consent);
            return consent;
        } catch (IOException e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    @Override
    public Consent getConsent(XS2ARequest consentGetRequest) throws BankRequestFailedException {
        this.setUrl(this.url + String.format(GET_CONSENT, consentGetRequest.getConsentId()));
        this.createBody(RequestType.GET);
        this.createHeaders(consentGetRequest);

        try (Response response = this.execute()) {
            String responseBody = extractResponseBody(response, 200);

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Consent.class, new ConsentSerializer())
                    .registerTypeAdapter(Account.class, new AccountSerializer())
                    .create();

            Consent consent = gson.fromJson(responseBody, Consent.class);
            consent.setId(consentGetRequest.getConsentId());
            return consent;
        } catch (IOException e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    @Override
    public Consent.State getStatus(XS2ARequest consentStatusRequest) throws BankRequestFailedException {
        this.setUrl(this.url + String.format(GET_CONSENT_STATUS, consentStatusRequest.getConsentId()));
        this.createBody(RequestType.GET);
        this.createHeaders(consentStatusRequest);

        try (Response response = this.execute()) {
            String responseBody = extractResponseBody(response, 200);

            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .registerTypeAdapter(Consent.State.class, new ConsentStatusSerializer())
                    .create();
            return gson.fromJson(responseBody, Consent.State.class);
        } catch (IOException e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    @Override
    public Consent deleteConsent(XS2ARequest consentDeleteRequest) throws BankRequestFailedException {
        this.setUrl(this.url + String.format(DELETE_CONSENT, consentDeleteRequest.getConsentId()));
        this.createBody(RequestType.DELETE);
        this.createHeaders(consentDeleteRequest);

        try (Response response = this.execute()) {
            extractResponseBody(response, 204, false);

            Consent consent = new Consent();
            consent.setId(consentDeleteRequest.getConsentId());
            return new PersistentConsent().updateState(consent, Consent.State.TERMINATED_BY_TPP);
        } catch (IOException e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    @Override
    public void updatePSUData(XS2ARequest consentUpdatePSUDataRequest) throws BankRequestFailedException {
        this.setUrl(this.url + String.format(UPDATE_PSU_DATA, consentUpdatePSUDataRequest.getConsentId(), ((ConsentUpdatePSUDataRequest) consentUpdatePSUDataRequest).getAuthorisationId()));
        this.createBody(RequestType.PUT, JSON, consentUpdatePSUDataRequest);
        this.createHeaders(consentUpdatePSUDataRequest);

        try (Response response = this.execute()) {
            //TODO Für den EMBEDDED Approach muss hier vervollständigt werden
        } catch (IOException e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    @Override
    public SCA startAuthorisation(XS2ARequest request) throws BankRequestFailedException {
        return super.startAuthorisation(request);
    }

    @Override
    public List<String> getAuthorisationRequest(XS2ARequest request) throws BankRequestFailedException {
        return super.getAuthorisationRequest(request);
    }

    @Override
    public String getSCAStatus(XS2ARequest request) throws BankRequestFailedException {
        return super.getSCAStatus(request);
    }
}
