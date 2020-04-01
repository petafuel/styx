package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.persistence.layers.PersistentConsent;
import net.petafuel.styx.core.xs2a.contracts.CSInterface;
import net.petafuel.styx.core.xs2a.contracts.IXS2AHttpSigner;
import net.petafuel.styx.core.xs2a.contracts.XS2AAuthorisationRequest;
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
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.BasicAuthorisationService;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.AuthoriseTransactionRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.GetAuthorisationsRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.GetSCAStatusRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.SelectAuthenticationMethodRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.StartAuthorisationRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.UpdatePSUAuthenticationRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.UpdatePSUIdentificationRequest;
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

            Consent consentFromResponse = gson.fromJson(responseBody, Consent.class);
            consentFromResponse.setId(consentGetRequest.getConsentId());

            Consent consentFromDatabase = new PersistentConsent().get(consentFromResponse);
            consentFromDatabase.setAccess(consentFromResponse.getAccess());
            consentFromDatabase.setRecurringIndicator(consentFromResponse.isRecurringIndicator());
            consentFromDatabase.setValidUntil(consentFromResponse.getValidUntil());
            consentFromDatabase.setFrequencyPerDay(consentFromResponse.getFrequencyPerDay());
            consentFromDatabase.setState(consentFromResponse.getState());
            consentFromDatabase.setLastAction(consentFromResponse.getLastAction());

            return new PersistentConsent().update(consentFromDatabase);
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
            PersistentConsent persistentConsent = new PersistentConsent();
            Consent.State state = gson.fromJson(responseBody, Consent.State.class);
            Consent consent = new Consent();
            consent.setId(consentStatusRequest.getConsentId());
            persistentConsent.updateState(consent, state);
            return state;
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
    public SCA startAuthorisation(XS2AAuthorisationRequest xs2ARequest) throws BankRequestFailedException {
        return super.startAuthorisation((StartAuthorisationRequest) xs2ARequest);
    }

    @Override
    public List<String> getAuthorisations(XS2AAuthorisationRequest xs2AAuthorisationRequest) throws BankRequestFailedException {
        return super.getAuthorisations((GetAuthorisationsRequest) xs2AAuthorisationRequest);
    }

    @Override
    public SCA.Status getSCAStatus(XS2AAuthorisationRequest xs2AAuthorisationRequest) throws BankRequestFailedException {
        return super.getSCAStatus((GetSCAStatusRequest) xs2AAuthorisationRequest);
    }

    @Override
    public SCA updatePSUIdentification(XS2AAuthorisationRequest xs2ARequest) throws BankRequestFailedException {
        return super.updatePSUIdentification((UpdatePSUIdentificationRequest) xs2ARequest);
    }

    @Override
    public SCA updatePSUAuthentication(XS2AAuthorisationRequest xs2ARequest) throws BankRequestFailedException {
        return super.updatePSUAuthentication((UpdatePSUAuthenticationRequest) xs2ARequest);
    }

    @Override
    public SCA selectAuthenticationMethod(XS2AAuthorisationRequest xs2ARequest) throws BankRequestFailedException {
        return super.selectAuthenticationMethod((SelectAuthenticationMethodRequest) xs2ARequest);
    }

    @Override
    public SCA authoriseTransaction(XS2AAuthorisationRequest xs2AAuthorisationRequest) throws BankRequestFailedException {
        return super.authoriseTransaction((AuthoriseTransactionRequest) xs2AAuthorisationRequest);
    }
}
