package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3;

import net.petafuel.styx.core.persistence.layers.PersistentConsent;
import net.petafuel.styx.core.xs2a.contracts.AISRequest;
import net.petafuel.styx.core.xs2a.contracts.CSInterface;
import net.petafuel.styx.core.xs2a.contracts.IXS2AHttpSigner;
import net.petafuel.styx.core.xs2a.contracts.SCARequest;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.ConsentStatus;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.sca.SCAUtils;
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

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyNamingStrategy;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class BerlinGroupCS extends BasicAuthorisationService implements CSInterface {
    private static final Logger LOG = LogManager.getLogger(BerlinGroupCS.class);

    public BerlinGroupCS(String url, IXS2AHttpSigner signer) {
        super(LOG, url, signer);
    }

    public BerlinGroupCS(Logger log, String url, IXS2AHttpSigner signer) {
        super(log, url, signer);
    }

    public Consent createConsent(AISRequest consentRequest) throws BankRequestFailedException {
        this.setUrl(this.url + consentRequest.getServicePath());
        this.createBody(RequestType.POST, JSON, consentRequest);
        this.createHeaders(consentRequest);

        JsonbConfig config = new JsonbConfig()
                .withPropertyNamingStrategy(PropertyNamingStrategy.CASE_INSENSITIVE);

        try (Response response = this.execute(); Jsonb jsonb = JsonbBuilder.create(config)) {
            String responseBody = extractResponseBody(response, 201);

            Consent consent = jsonb.fromJson(responseBody, Consent.class);
            consent.setxRequestId(UUID.fromString(consentRequest.getHeaders().get("x-request-id")));
            consent.setPsu(consentRequest.getPsu());
            //if the sca method was not set by previously parsing the body, use the bank supplied header
            consent.getSca().setApproach(SCAUtils.parseSCAApproach(consent.getLinks(), response));
            consent.setAccess(consentRequest.getConsent().getAccess());
            new PersistentConsent().create(consent);
            return consent;
        } catch (BankRequestFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    @Override
    public Consent getConsent(AISRequest consentGetRequest) throws BankRequestFailedException {
        this.setUrl(this.url + consentGetRequest.getServicePath());
        this.createBody(RequestType.GET);
        this.createHeaders(consentGetRequest);

        try (Response response = this.execute(); Jsonb jsonb = JsonbBuilder.create()) {
            String responseBody = extractResponseBody(response, 200);

            Consent consentFromResponse = jsonb.fromJson(responseBody, Consent.class);
            consentFromResponse.setId(consentGetRequest.getConsentId());

            Consent consentFromDatabase = new PersistentConsent().get(consentFromResponse);
            consentFromDatabase.setAccess(consentFromResponse.getAccess());
            consentFromDatabase.setRecurringIndicator(consentFromResponse.isRecurringIndicator());
            consentFromDatabase.setValidUntil(consentFromResponse.getValidUntil());
            consentFromDatabase.setFrequencyPerDay(consentFromResponse.getFrequencyPerDay());
            consentFromDatabase.setState(consentFromResponse.getState());
            consentFromDatabase.setLastAction(consentFromResponse.getLastAction());

            return new PersistentConsent().update(consentFromDatabase);
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    @Override
    public ConsentStatus getStatus(AISRequest consentStatusRequest) throws BankRequestFailedException {
        this.setUrl(this.url + consentStatusRequest.getServicePath());
        this.createBody(RequestType.GET);
        this.createHeaders(consentStatusRequest);

        try (Response response = this.execute(); Jsonb jsonb = JsonbBuilder.create()) {
            String responseBody = extractResponseBody(response, 200);

            PersistentConsent persistentConsent = new PersistentConsent();
            Consent consent = jsonb.fromJson(responseBody, Consent.class);
            consent.setId(consentStatusRequest.getConsentId());
            persistentConsent.updateState(consent, consent.getState());
            return consent.getState();
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    @Override
    public Consent deleteConsent(AISRequest consentDeleteRequest) throws BankRequestFailedException {
        this.setUrl(this.url + consentDeleteRequest.getServicePath());
        this.createBody(RequestType.DELETE);
        this.createHeaders(consentDeleteRequest);

        try (Response response = this.execute()) {
            extractResponseBody(response, 204, false);

            Consent consent = new Consent();
            consent.setId(consentDeleteRequest.getConsentId());
            return new PersistentConsent().updateState(consent, ConsentStatus.TERMINATED_BY_TPP);
        } catch (IOException e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }


    @Override
    public SCA startAuthorisation(SCARequest xs2ARequest) throws BankRequestFailedException {
        return super.startAuthorisation((StartAuthorisationRequest) xs2ARequest);
    }

    @Override
    public List<String> getAuthorisations(SCARequest xs2AAuthorisationRequest) throws BankRequestFailedException {
        return super.getAuthorisations((GetAuthorisationsRequest) xs2AAuthorisationRequest);
    }

    @Override
    public SCA.Status getSCAStatus(SCARequest xs2AAuthorisationRequest) throws BankRequestFailedException {
        return super.getSCAStatus((GetSCAStatusRequest) xs2AAuthorisationRequest);
    }

    @Override
    public SCA updatePSUIdentification(SCARequest xs2ARequest) throws BankRequestFailedException {
        return super.updatePSUIdentification((UpdatePSUIdentificationRequest) xs2ARequest);
    }

    @Override
    public SCA updatePSUAuthentication(SCARequest xs2ARequest) throws BankRequestFailedException {
        return super.updatePSUAuthentication((UpdatePSUAuthenticationRequest) xs2ARequest);
    }

    @Override
    public SCA selectAuthenticationMethod(SCARequest xs2ARequest) throws BankRequestFailedException {
        return super.selectAuthenticationMethod((SelectAuthenticationMethodRequest) xs2ARequest);
    }

    @Override
    public SCA authoriseTransaction(SCARequest xs2AAuthorisationRequest) throws BankRequestFailedException {
        return super.authoriseTransaction((AuthoriseTransactionRequest) xs2AAuthorisationRequest);
    }
}
