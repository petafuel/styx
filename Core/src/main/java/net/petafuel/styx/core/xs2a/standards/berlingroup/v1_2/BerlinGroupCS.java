package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.contracts.CSInterface;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.CreateConsentResponse;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.DeleteConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.GetConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.StatusConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.serializers.ConsentSerializer;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.security.SignatureException;

public class BerlinGroupCS extends BasicService implements CSInterface {

    private static final Logger LOG = LogManager.getLogger(BerlinGroupCS.class);

    private static final String POST_CONSENT = "/v1/consents";
    private static final String GET_CONSENT = "/v1/consents/%s";
    private static final String GET_CONSENT_STATUS = "/v1/consents/%s/status";
    private static final String DELETE_CONSENT = "/v1/consents/%s";

    public BerlinGroupCS(String url) {
        super(url);
    }

    public Consent createConsent(XS2ARequest consentRequest) throws SignatureException, BankRequestFailedException {
        this.setUrl(this.url + POST_CONSENT);
        this.createBody(RequestType.POST, JSON, consentRequest);
        this.createHeaders(consentRequest);

        try (Response response = this.execute()) {

            if (response.code() != 201) {
                String msg = "Request failed with ResponseCode {} -> {}";
                if (response.body() == null) {
                    LOG.error(msg, response.code(), "empty response body");
                    throw new BankRequestFailedException("empty response body", response.code());
                } else {
                    String responseMessage = response.body().string();
                    LOG.error(msg, response.code(), responseMessage);
                    throw new BankRequestFailedException(responseMessage, response.code());
                }
            }

            ResponseBody responseBody = response.body();

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Consent.class, new ConsentSerializer())
                    .create();
            Consent consent = gson.fromJson(responseBody.string(), Consent.class);
            consent.setRequest(consentRequest);
            consent.getSca().setApproach(SCA.Approach.valueOf(response.header("ASPSP-SCA-Approach")));
            return consent;
        } catch (IOException e) {
            throw new BankRequestFailedException(e.getMessage());
        }
    }

    @Override
    public Consent getConsent(XS2ARequest consentGetRequest) throws BankRequestFailedException {
        this.setUrl(this.url + String.format(GET_CONSENT, ((GetConsentRequest) consentGetRequest).getConsentId()));
        this.createBody(RequestType.GET, JSON, consentGetRequest);
        this.createHeaders(consentGetRequest);

        try (Response response = this.execute()) {

            if (response.code() != 200) {
                String msg = "Request failed with ResponseCode {} -> {}";
                if (response.body() == null) {
                    LOG.error(msg, response.code(), "empty response body");
                    throw new BankRequestFailedException("empty response body", response.code());
                } else {
                    String responseMessage = response.body().string();
                    LOG.error(msg, response.code(), responseMessage);
                    throw new BankRequestFailedException(responseMessage, response.code());
                }
            }

            ResponseBody responseBody = response.body();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Consent.class, new ConsentSerializer())
                    .create();
            Consent consent = gson.fromJson(responseBody.string(), Consent.class);
            consent.setRequest(consentGetRequest);
            consent.getSca().setApproach(SCA.Approach.valueOf(response.header("ASPSP-SCA-Approach")));
            return consent;
        } catch (IOException e) {
            throw new BankRequestFailedException(e.getMessage());
        }
    }

    @Override
    public Consent.State getStatus(XS2ARequest consentStatusRequest) throws BankRequestFailedException {
        this.setUrl(this.url + String.format(GET_CONSENT_STATUS, ((StatusConsentRequest) consentStatusRequest).getConsentId()));
        this.createBody(RequestType.GET, JSON, consentStatusRequest);
        this.createHeaders(consentStatusRequest);

        try (Response response = this.execute()) {

            if (response.code() != 200) {
                String msg = "Request failed with ResponseCode {} -> {}";
                if (response.body() == null) {
                    LOG.error(msg, response.code(), "empty response body");
                    throw new BankRequestFailedException("empty response body", response.code());
                } else {
                    String responseMessage = response.body().string();
                    LOG.error(msg, response.code(), responseMessage);
                    throw new BankRequestFailedException(responseMessage, response.code());
                }
            }

            ResponseBody responseBody = response.body();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Consent.class, new ConsentSerializer())
                    .create();
            Consent consent = gson.fromJson(responseBody.string(), Consent.class);

            return consent.getState();
        } catch (IOException e) {
            throw new BankRequestFailedException(e.getMessage());
        }
    }

    @Override
    public Consent deleteConsent(XS2ARequest consentDeleteRequest) throws BankRequestFailedException {
        this.setUrl(this.url + String.format(DELETE_CONSENT, ((DeleteConsentRequest) consentDeleteRequest).getConsentId()));
        this.createBody(RequestType.DELETE, JSON, consentDeleteRequest);
        this.createHeaders(consentDeleteRequest);

        try (Response response = this.execute()) {

            if (response.code() != 204) {
                String msg = "Request failed with ResponseCode {} -> {}";
                if (response.body() == null) {
                    LOG.error(msg, response.code(), "empty response body");
                    throw new BankRequestFailedException("empty response body", response.code());
                } else {
                    String responseMessage = response.body().string();
                    LOG.error(msg, response.code(), responseMessage);
                    throw new BankRequestFailedException(responseMessage, response.code());
                }
            }
            Consent consent = new Consent();
            consent.setRequest(consentDeleteRequest);
            consent.setConsentId(((DeleteConsentRequest) consentDeleteRequest).getConsentId());
            consent.setState(Consent.State.TERMINATED_BY_TPP);
            return consent;
        } catch (IOException e) {
            throw new BankRequestFailedException(e.getMessage());
        }
    }
}
