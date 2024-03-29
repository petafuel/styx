package net.petafuel.styx.core.xs2a.oauth;

import net.petafuel.styx.core.banklookup.sad.entities.Url;
import net.petafuel.styx.core.persistence.layers.PersistentOAuthSession;
import net.petafuel.styx.core.xs2a.callback.control.CallbackProvider;
import net.petafuel.styx.core.xs2a.callback.entity.RealmParameter;
import net.petafuel.styx.core.xs2a.callback.entity.ServiceRealm;
import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.entities.StrongAuthenticatableResource;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.exceptions.OAuthTokenExpiredException;
import net.petafuel.styx.core.xs2a.oauth.entities.OAuthSession;
import net.petafuel.styx.core.xs2a.oauth.http.OAuthTokenRequest;
import net.petafuel.styx.core.xs2a.oauth.http.RefreshTokenRequest;
import net.petafuel.styx.core.xs2a.oauth.serializers.EndpointsSerializer;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.BerlinGroupSigner;
import net.petafuel.styx.core.xs2a.utils.Config;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
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

    public static String generateCodeVerifier() {

        SecureRandom sr = new SecureRandom();
        byte[] code = new byte[32];
        sr.nextBytes(code);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(code);
    }

    public static String getCodeChallengeFromState(String state) {
        OAuthSession session = PersistentOAuthSession.getByState(state);
        return generateCodeChallenge(session.getCodeVerifier());
    }

    //SHA-256 is predefined for key exchange on oAuth 2.0 @see https://tools.ietf.org/html/rfc7636#section-4.2
    @SuppressWarnings("squid:S4790")
    private static String generateCodeChallenge(String codeVerifier) {
        try {
            byte[] bytes = codeVerifier.getBytes(StandardCharsets.US_ASCII);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(bytes, 0, bytes.length);
            byte[] digest = md.digest();
            String codeChallenge = Base64.getUrlEncoder().encodeToString(digest);
            codeChallenge = codeChallenge.replace('+', '-')
                    .replace('/', '_')
                    .replace("=", "");
            return codeChallenge;
        } catch (Exception e) {
            return "";
        }
    }

    public static String buildLink(String state, UUID xRequestId, ServiceRealm serviceRealm) {
        OAuthSession stored = PersistentOAuthSession.getByState(state);
        HashMap<String, String> queryParams = getQueryParameters(stored);
        Properties properties = Config.getInstance().getProperties();
        queryParams.put("client_id", properties.getProperty("keystore.client_id"));
        queryParams.put("redirect_uri", CallbackProvider.generateCallbackUrl(serviceRealm, RealmParameter.OK, xRequestId.toString()));
        return stored.getAuthorizationEndpoint() + BasicService.httpBuildQuery(queryParams);
    }

    public static String buildLink(String state, String bic) {
        OAuthSession stored = PersistentOAuthSession.getByState(state);
        HashMap<String, String> queryParams = getQueryParameters(stored);
        queryParams.put("bic", bic);
        Properties properties = Config.getInstance().getProperties();
        queryParams.put("client_id", properties.getProperty("keystore.client_id"));
        queryParams.put("redirect_uri", CallbackProvider.generateCallbackUrl(ServiceRealm.OAUTH, RealmParameter.PREAUTH, stored.getState()));
        return stored.getAuthorizationEndpoint() + BasicService.httpBuildQuery(queryParams);
    }

    private static HashMap<String, String> getQueryParameters(OAuthSession oAuthSession) {

        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("response_type", "code");
        queryParams.put("scope", oAuthSession.getScope());
        queryParams.put("state", oAuthSession.getState());
        queryParams.put("code_challenge", OAuthService.generateCodeChallenge(oAuthSession.getCodeVerifier()));
        queryParams.put("code_challenge_method", "S256");

        return queryParams;
    }

    public static OAuthSession startSession(StrongAuthenticatableResource strongAuthenticatableResource, String scope) {

        OAuthService service = new OAuthService();
        Map<String, String> endpoints = service.getEndpoints(strongAuthenticatableResource.getLinks().getScaOAuth().getUrl());
        OAuthSession session = OAuthSession.start(strongAuthenticatableResource.getxRequestId());
        session.setScope(scope);
        session.setAuthorizationEndpoint(endpoints.get("authorization_endpoint"));
        session.setTokenEndpoint(endpoints.get("token_endpoint"));

        return PersistentOAuthSession.create(session);
    }

    public static OAuthSession startPreAuthSession(Url url, String scope) {
        OAuthSession session = OAuthSession.start(null); // The X-Request-Id is not relevant for the prestep, since the OAuthSession will not directly relate to a consent or payment
        session.setScope(scope);
        session.setAuthorizationEndpoint(url.getPreauthAuthorizationEndpoint());
        session.setTokenEndpoint(url.getPreauthTokenEndpoint());
        session.setxRequestId(session.getId());

        return PersistentOAuthSession.create(session);
    }

    public OAuthSession tokenRequest(String url, OAuthTokenRequest request) throws BankRequestFailedException {

        this.setUrl(url);
        if (request.isJsonBody()) {
            this.createBody(RequestType.POST, JSON, request);
        } else {
            this.createBody(RequestType.POST, FORM_URLENCODED, request);
        }

        try (Response response = this.execute(); Jsonb jsonb = JsonbBuilder.create()) {
            String body = extractResponseBody(response, 200);
            return jsonb.fromJson(body, OAuthSession.class);
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    /**
     * @param url should be a .well-known url within oauth flow
     * @return HashMap<String, String>
     */
    private Map<String, String> getEndpoints(String url) {
        this.setUrl(url);
        this.createBody(RequestType.GET);

        JsonbConfig jsonbConfig = new JsonbConfig();
        jsonbConfig.withDeserializers(new EndpointsSerializer());
        try (Response response = this.execute(); Jsonb jsonb = JsonbBuilder.create(jsonbConfig)) {
            String body = extractResponseBody(response, 200);
            return jsonb.fromJson(body, HashMap.class);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    public static OAuthSession refreshToken(OAuthSession oAuthSession) throws OAuthTokenExpiredException {
        String state = oAuthSession.getState();
        RefreshTokenRequest request = new RefreshTokenRequest(oAuthSession.getRefreshToken());
        OAuthService service = new OAuthService();
        try {
            oAuthSession = service.tokenRequest(oAuthSession.getTokenEndpoint(), request);
            oAuthSession.setState(state);
            PersistentOAuthSession.update(oAuthSession);
            return oAuthSession;
        } catch (BankRequestFailedException expiredToken) {
            throw new OAuthTokenExpiredException(OAuthTokenExpiredException.MESSAGE);
        }
    }
}
