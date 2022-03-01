package net.petafuel.styx.core.xs2a.standards.ing.v1_0.services;

import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.ing.v1_0.entities.AccessToken;
import net.petafuel.styx.core.xs2a.standards.ing.v1_0.http.AccessTokenRequest;
import net.petafuel.styx.core.xs2a.standards.ing.v1_0.INGSigner;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

public class AccessTokenService extends BasicService {

    private static final Logger LOG = LogManager.getLogger(AccessTokenService.class);

    public AccessTokenService() {
        super(LOG, null, new INGSigner());
    }

    /**
     * call accessToken endpoint of ING and return AccessToken data
     * @param url
     * @param request
     * @return
     * @throws BankRequestFailedException
     */
    public AccessToken tokenRequest(String url, AccessTokenRequest request) throws BankRequestFailedException {

        this.setUrl(url);
        this.createHeaders(request);
        this.createBody(RequestType.POST, FORM_URLENCODED, request);

        try (Response response = this.execute(); Jsonb jsonb = JsonbBuilder.create()) {
            String body = extractResponseBody(response, 200);
            return jsonb.fromJson(body, AccessToken.class);
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }
}
