package net.petafuel.styx.core.xs2a.contracts;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.sca.SCAUtils;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.StartAuthorisationRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers.SCASerializer;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BasicAuthorisationService extends BasicService {

    private static final String AUTHORISATIONS = "/v1/%s/authorisations";

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
}
