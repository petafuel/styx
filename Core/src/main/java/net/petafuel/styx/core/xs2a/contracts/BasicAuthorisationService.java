package net.petafuel.styx.core.xs2a.contracts;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.sca.SCAUtils;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.GetAuthorisationRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.GetAuthorisationResponse;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.GetSCAStatusRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.GetSCAStatusResponse;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.StartAuthorisationRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers.SCASerializer;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.List;

public class BasicAuthorisationService extends BasicService {

    private static final String AUTHORISATIONS = "/v1/%s/authorisations";
    private static final String PIS_AUTHORISATION_REQUEST = "/v1/%s/%s/%s/authorisations";
    private static final String AIS_AUTHORISATION_REQUEST = "/v1/consents/%s/authorisations";
    private static final String PIS_SCA_STATUS = "/v1/%s/%s/%s/authorisations/%s";
    private static final String AIS_SCA_STATUS = "/v1/consents/%s/authorisations/%s";

    public BasicAuthorisationService(String url, IXS2AHttpSigner signer) {
        this(LogManager.getLogger(BasicAuthorisationService.class), url, signer);
    }

    public BasicAuthorisationService(Logger log, String url, IXS2AHttpSigner signer) {
        super(log, url, signer);
    }

    protected SCA startAuthorisation(XS2ARequest xs2ARequest) throws BankRequestFailedException {

        StartAuthorisationRequest request = (StartAuthorisationRequest) xs2ARequest;

        this.setUrl(this.url + String.format(AUTHORISATIONS, request.getService()));

        this.createBody(RequestType.POST, JSON, request);
        this.createHeaders(request);

        try (Response response = this.execute()) {
            String responseBody = extractResponseBody(response, request.getExpectedResponseCode());
            Gson gson = new GsonBuilder().registerTypeAdapter(SCA.class, new SCASerializer()).create();
            SCA sca = gson.fromJson(responseBody, SCA.class);
            SCAUtils.parseSCAApproach(sca, response);
            return sca;
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    protected List<String> getAuthorisationRequest(XS2ARequest xs2ARequest) throws BankRequestFailedException {
        GetAuthorisationRequest request = (GetAuthorisationRequest) xs2ARequest;
        if (request.getType().equals("AIS")) {
            this.setUrl(this.url + String.format(AIS_AUTHORISATION_REQUEST, request.getConsentId()));
        } else {
            this.setUrl(this.url + String.format(PIS_AUTHORISATION_REQUEST, request.getPaymentService(),
                    request.getPaymentProduct(), request.getPaymentId())
            );
        }

        this.createBody(RequestType.GET);
        this.createHeaders(request);

        try (Response response = this.execute(); Jsonb jsonb = JsonbBuilder.create()) {
            String responseBody = extractResponseBody(response, 200);
            GetAuthorisationResponse getAuthorisationResponse = jsonb.fromJson(responseBody, GetAuthorisationResponse.class);
            return getAuthorisationResponse.getAuthorisationIds();
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    protected String getSCAStatus(XS2ARequest xs2ARequest) throws BankRequestFailedException {
        GetSCAStatusRequest request = (GetSCAStatusRequest) xs2ARequest;
        if (request.getType().equals("AIS")) {
            this.setUrl(this.url + String.format(AIS_SCA_STATUS, request.getConsentId(), request.getAuthorisationId()));
        } else {
            this.setUrl(this.url + String.format(PIS_SCA_STATUS, request.getPaymentService(),
                    request.getPaymentProduct(), request.getPaymentId(), request.getAuthorisationId())
            );
        }

        this.createBody(RequestType.GET);
        this.createHeaders(request);

        try (Response response = this.execute(); Jsonb jsonb = JsonbBuilder.create()) {
            String responseBody = extractResponseBody(response, 200);
            GetSCAStatusResponse getSCAStatusResponse = jsonb.fromJson(responseBody, GetSCAStatusResponse.class);
            return getSCAStatusResponse.getScaStatus();
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }
}
