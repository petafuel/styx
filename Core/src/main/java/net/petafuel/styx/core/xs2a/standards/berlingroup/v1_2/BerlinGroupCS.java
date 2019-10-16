package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.persistence.layers.PersistentConsent;
import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.contracts.CSInterface;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.contracts.IBerlinGroupSigner;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.CreateConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.DeleteConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.GetConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.StatusConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.serializers.AccountSerializer;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.serializers.ConsentSerializer;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.serializers.ConsentStatusSerializer;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.UUID;

public class BerlinGroupCS extends BasicService implements CSInterface {

    private static final Logger LOG = LogManager.getLogger(BerlinGroupCS.class);

    private static final String POST_CONSENT = "/v1/consents";
    private static final String GET_CONSENT = "/v1/consents/%s";
    private static final String GET_CONSENT_STATUS = "/v1/consents/%s/status";
    private static final String DELETE_CONSENT = "/v1/consents/%s";

    public BerlinGroupCS(String url, IBerlinGroupSigner signer) {
        super(LOG, url, signer);
    }

    public Consent createConsent(XS2ARequest consentRequest) throws BankRequestFailedException {
        this.setUrl(this.url + POST_CONSENT);
        this.createBody(RequestType.POST, JSON, consentRequest);
        this.createHeaders(consentRequest);

        try (Response response = this.execute()) {

            ResponseBody responseBody = response.body();

            if (response.code() != 201 || responseBody == null) {
                throwBankRequestException(response);
            }

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Consent.class, new ConsentSerializer())
                    .create();
            Consent consent = gson.fromJson(responseBody.string(), Consent.class);
            consent.setxRequestId(UUID.fromString(consentRequest.getHeaders().get("x-request-id")));
            consent.setPsu(((CreateConsentRequest) consentRequest).getConsent().getPsu());
            //if the sca method was not set by previously parsing the body, use the bank supplied header
            if (consent.getSca().getApproach() == null) {
                consent.getSca().setApproach(SCA.Approach.valueOf(response.header("ASPSP-SCA-Approach")));
            }
            consent.setAccess(((CreateConsentRequest) consentRequest).getConsent().getAccess());
            new PersistentConsent().create(consent);
            return consent;
        } catch (IOException e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    @Override
    public Consent getConsent(XS2ARequest consentGetRequest) throws BankRequestFailedException {
        this.setUrl(this.url + String.format(GET_CONSENT, ((GetConsentRequest) consentGetRequest).getConsentId()));
        this.createBody(RequestType.GET);
        this.createHeaders(consentGetRequest);

        try (Response response = this.execute()) {

            ResponseBody responseBody = response.body();
            if (response.code() != 200 || responseBody == null) {
                throwBankRequestException(response);
            }

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Consent.class, new ConsentSerializer())
                    .registerTypeAdapter(Account.class, new AccountSerializer())
                    .create();

            Consent consent = gson.fromJson(responseBody.string(), Consent.class);
            consent.setId(((GetConsentRequest) consentGetRequest).getConsentId());
            return consent;
        } catch (IOException e) {
            throw new BankRequestFailedException(e.getMessage(),e);
        }
    }

    @Override
    public Consent.State getStatus(XS2ARequest consentStatusRequest) throws BankRequestFailedException {
        this.setUrl(this.url + String.format(GET_CONSENT_STATUS, ((StatusConsentRequest) consentStatusRequest).getConsentId()));
        this.createBody(RequestType.GET);
        this.createHeaders(consentStatusRequest);

        try (Response response = this.execute()) {

            ResponseBody responseBody = response.body();
            if (response.code() != 200 || responseBody == null) {
                throwBankRequestException(response);
            }

            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .registerTypeAdapter(Consent.State.class, new ConsentStatusSerializer())
                    .create();
            return gson.fromJson(responseBody.string(), Consent.State.class);
        } catch (IOException e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    @Override
    public Consent deleteConsent(XS2ARequest consentDeleteRequest) throws BankRequestFailedException {
        this.setUrl(this.url + String.format(DELETE_CONSENT, ((DeleteConsentRequest) consentDeleteRequest).getConsentId()));
        this.createBody(RequestType.DELETE, JSON, consentDeleteRequest);
        this.createHeaders(consentDeleteRequest);

        try (Response response = this.execute()) {

            if (response.code() != 204) {
                throwBankRequestException(response);
            }
            Consent consent = new Consent();
            consent.setId(((DeleteConsentRequest) consentDeleteRequest).getConsentId());
            return new PersistentConsent().updateState(consent, Consent.State.TERMINATED_BY_TPP);
        } catch (IOException e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }
}
