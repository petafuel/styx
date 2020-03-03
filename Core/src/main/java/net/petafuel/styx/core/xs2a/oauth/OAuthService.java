package net.petafuel.styx.core.xs2a.oauth;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.persistence.layers.PersistentOAuthSession;
import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.oauth.entities.OAuthSession;
import net.petafuel.styx.core.xs2a.oauth.http.TokenRequest;
import net.petafuel.styx.core.xs2a.oauth.serializers.EndpointsSerializer;
import net.petafuel.styx.core.xs2a.oauth.serializers.TokenSerializer;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.BerlinGroupSigner;
import net.petafuel.styx.core.xs2a.utils.Config;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class OAuthService extends BasicService {

    private static final Logger LOG = LogManager.getLogger(OAuthService.class);

    public OAuthService() {
        super(LOG, null, new BerlinGroupSigner());
    }

    public OAuthSession accessTokenRequest(String url, TokenRequest request) throws BankRequestFailedException {

        this.setUrl(url);
        this.createBody(RequestType.POST, JSON, request);

        try (Response response = this.execute()) {
            String body = response.body().string();

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(OAuthSession.class, new TokenSerializer())
                    .create();
            return gson.fromJson(body, OAuthSession.class);
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    public Map<String, String> getEndpoints(String url){
        this.setUrl(url);
        this.createBody(RequestType.GET);

        try (Response response = this.execute()) {
            String body = response.body().string();

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(HashMap.class, new EndpointsSerializer())
                    .create();
            return gson.fromJson(body, HashMap.class);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    public static String generateState() {
        return UUID.randomUUID().toString();
    }

    public static String generateCodeVerifier() {

        SecureRandom sr = new SecureRandom();
        byte[] code = new byte[32];
        sr.nextBytes(code);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(code);
    }

    //SHA-256 is predefined for key exchange on oAuth 2.0 @see https://tools.ietf.org/html/rfc7636#section-4.2
    @SuppressWarnings("squid:S4790")
    private static String generateCodeChallenge(String codeVerifier) {
        try {
            byte[] bytes = codeVerifier.getBytes(StandardCharsets.US_ASCII);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(bytes, 0, bytes.length);
            byte[] digest = md.digest();
            return  Base64.getUrlEncoder().encodeToString(digest);
        } catch (Exception e) {
            return "";
        }
    }

    public static String buildLink(String state) {

        OAuthSession stored = new PersistentOAuthSession().get(state);

        Properties properties = Config.getInstance().getProperties();

        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("client_id", properties.getProperty("keystore.client_id"));
        queryParams.put("response_type", "code");
        queryParams.put("scope", stored.getScope());
        queryParams.put("redirect_uri", properties.getProperty("styx.redirect.baseurl"));
        queryParams.put("state", state);
        queryParams.put("code_challenge", OAuthService.generateCodeChallenge(stored.getCodeVerifier()));
        queryParams.put("code_challenge_method", "S256");

        return stored.getAuthorizationEndpoint() + BasicService.httpBuildQuery(queryParams);
    }

    public static OAuthSession startSession(SCA sca, String scope) {

        OAuthService service = new OAuthService();
        Map<String, String> endpoints = service.getEndpoints(sca.getLinks().get(SCA.LinkType.SCA_OAUTH));
        String state = OAuthService.generateState();
        String codeVerifier = OAuthService.generateCodeVerifier();
        OAuthSession session = new OAuthSession();
        session.setCodeVerifier(codeVerifier);
        session.setScope(scope);
        session.setAuthorizationEndpoint(endpoints.get("authorization_endpoint"));
        session.setTokenEndpoint(endpoints.get("token_endpoint"));
        session.setState(state);

        return new PersistentOAuthSession().create(session);
    }
}
