package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.contracts.IXS2AHttpSigner;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.sca.SCAUtils;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.AuthoriseTransactionRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.GetAuthorisationsRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.GetAuthorisationResponse;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.GetSCAStatusRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.GetSCAStatusResponse;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.SelectAuthenticationMethodRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.StartAuthorisationRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.UpdatePSUAuthenticationRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.UpdatePSUIdentificationRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers.SCASerializer;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.List;

public abstract class BasicAuthorisationService extends BasicService {
    public BasicAuthorisationService(String url, IXS2AHttpSigner signer) {
        this(LogManager.getLogger(BasicAuthorisationService.class), url, signer);
    }

    public BasicAuthorisationService(Logger log, String url, IXS2AHttpSigner signer) {
        super(log, url, signer);
    }

    protected SCA startAuthorisation(StartAuthorisationRequest request) throws BankRequestFailedException {
        this.setUrl(this.url + request.getServiceURL());

        this.createBody(RequestType.POST, JSON, request);
        this.createHeaders(request);

        try (Response response = this.execute()) {
            String responseBody = extractResponseBody(response, 201);
            Gson gson = new GsonBuilder().registerTypeAdapter(SCA.class, new SCASerializer()).create();
            SCA sca = gson.fromJson(responseBody, SCA.class);
            SCAUtils.parseSCAApproach(sca, response);
            return sca;
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    protected List<String> getAuthorisations(GetAuthorisationsRequest xs2ARequest) throws BankRequestFailedException {
        this.setUrl(this.url + xs2ARequest.getServiceURL());
        this.createBody(RequestType.GET);
        this.createHeaders(xs2ARequest);

        try (Response response = this.execute(); Jsonb jsonb = JsonbBuilder.create()) {
            String responseBody = extractResponseBody(response, 200);
            GetAuthorisationResponse getAuthorisationResponse = jsonb.fromJson(responseBody, GetAuthorisationResponse.class);
            return getAuthorisationResponse.getAuthorisationIds();
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    protected SCA.Status getSCAStatus(GetSCAStatusRequest xs2ARequest) throws BankRequestFailedException {
        this.setUrl(this.url + xs2ARequest.getServiceURL());
        this.createBody(RequestType.GET);
        this.createHeaders(xs2ARequest);

        try (Response response = this.execute(); Jsonb jsonb = JsonbBuilder.create()) {
            String responseBody = extractResponseBody(response, 200);
            GetSCAStatusResponse getSCAStatusResponse = jsonb.fromJson(responseBody, GetSCAStatusResponse.class);
            return getSCAStatusResponse.getScaStatus();
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    protected SCA updatePSUIdentification(UpdatePSUIdentificationRequest updatePSUIdentificationRequest) throws BankRequestFailedException {
        this.setUrl(this.url + updatePSUIdentificationRequest.getServiceURL());
        this.createBody(RequestType.PUT, JSON, updatePSUIdentificationRequest);
        this.createHeaders(updatePSUIdentificationRequest);
        try (Response response = this.execute()) {
            String responseBody = extractResponseBody(response, 200);
            Gson gson = new GsonBuilder().registerTypeAdapter(SCA.class, new SCASerializer()).create();
            SCA sca = gson.fromJson(responseBody, SCA.class);
            SCAUtils.parseSCAApproach(sca, response);
            return sca;
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    protected SCA updatePSUAuthentication(UpdatePSUAuthenticationRequest updatePSUAuthenticationRequest) throws BankRequestFailedException {
        this.setUrl(this.url + updatePSUAuthenticationRequest.getServiceURL());
        this.createBody(RequestType.PUT, JSON, updatePSUAuthenticationRequest);
        this.createHeaders(updatePSUAuthenticationRequest);
        try (Response response = this.execute()) {
            String responseBody = extractResponseBody(response, 200);
            Gson gson = new GsonBuilder().registerTypeAdapter(SCA.class, new SCASerializer()).create();
            SCA sca = gson.fromJson(responseBody, SCA.class);
            SCAUtils.parseSCAApproach(sca, response);
            return sca;
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    protected SCA selectAuthenticationMethod(SelectAuthenticationMethodRequest selectAuthenticationMethodRequest) throws BankRequestFailedException {
        this.setUrl(this.url + selectAuthenticationMethodRequest.getServiceURL());
        this.createBody(RequestType.PUT, JSON, selectAuthenticationMethodRequest);
        this.createHeaders(selectAuthenticationMethodRequest);
        try (Response response = this.execute()) {
            String responseBody = extractResponseBody(response, 200);
            Gson gson = new GsonBuilder().registerTypeAdapter(SCA.class, new SCASerializer()).create();
            SCA sca = gson.fromJson(responseBody, SCA.class);
            SCAUtils.parseSCAApproach(sca, response);
            return sca;
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    protected SCA authoriseTransaction(AuthoriseTransactionRequest authoriseTransactionRequest) throws BankRequestFailedException {
        this.setUrl(this.url + authoriseTransactionRequest.getServiceURL());
        this.createBody(RequestType.PUT, JSON, authoriseTransactionRequest);
        this.createHeaders(authoriseTransactionRequest);
        try (Response response = this.execute()) {
            String responseBody = extractResponseBody(response, 200);
            Gson gson = new GsonBuilder().registerTypeAdapter(SCA.class, new SCASerializer()).create();
            SCA sca = gson.fromJson(responseBody, SCA.class);
            SCAUtils.parseSCAApproach(sca, response);
            return sca;
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }
}
