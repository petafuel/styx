package net.petafuel.styx.api.v1.callback.control;

import net.petafuel.styx.api.util.ApiProperties;
import net.petafuel.styx.core.persistence.layers.PersistentOAuthSession;
import net.petafuel.styx.core.xs2a.oauth.OAuthService;
import net.petafuel.styx.core.xs2a.oauth.entities.OAuthSession;
import net.petafuel.styx.core.xs2a.oauth.http.AuthorizationCodeRequest;
import net.petafuel.styx.core.xs2a.utils.Config;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class CallbackHandler {
    private static final Logger LOG = LogManager.getLogger(CallbackHandler.class);

    public Response handleRedirect(String xRequestId, HttpHeaders httpHeaders) {
        LOG.info("Handling callback request xrequsetid={}", xRequestId);
        StringBuilder output = new StringBuilder();
        for (String field : httpHeaders.getRequestHeaders().keySet()) {
            output.append(" ").append(field).append(": ").append(httpHeaders.getRequestHeader(field)).append("\n");
        }
        LOG.info("requestHeader={}", output);

        return this.returnHTMLPage();
    }

    public Response handleOAuth2(String code, String state, String error, String errorMessage) {
        if (error == null && handleSuccessfulOAuth2(code, state, OAuthService.SCA)) {
            return this.returnHTMLPage();
        } else {
            LOG.error("failed oauth2 callback error={}, errorMessage={}, state={}", error, errorMessage, state);
            return this.returnHTMLErrorPage();
        }
    }

    public Response handlePreStepOAuth2(String code, String state, String error, String errorMessage) {

        String baseUrl;
        if (!Boolean.parseBoolean(System.getProperty(ApiProperties.STYX_PROXY_ENABLED))) {
            baseUrl = System.getProperty(ApiProperties.STYX_PROXY_SCHEMA) + "//" +
                    System.getProperty(ApiProperties.STYX_PROXY_HOSTNAME + ":" +
                            System.getProperty(ApiProperties.STYX_PROXY_PORT));
        } else {
            baseUrl = "http://" + System.getProperty(ApiProperties.STYX_API_IP) + ":" + System.getProperty(ApiProperties.STYX_API_PORT);
        }

        OAuthSession oAuthSession = PersistentOAuthSession.getByState(state);
        if (error == null && handleSuccessfulOAuth2(code, state, OAuthService.PREAUTH)) {
            return Response.temporaryRedirect(URI.create(baseUrl + "/v1/preauth/success/" + oAuthSession.getId().toString())).build();
        } else {
            LOG.error("failed oauth2 callback error={}, errorMessage={}, state={}", error, errorMessage, state);
            return Response.temporaryRedirect(URI.create(baseUrl + "/v1/preauth/error/" + oAuthSession.getId().toString())).build();
        }
    }

    private boolean handleSuccessfulOAuth2(String code, String state, String oauthType) {
        OAuthService service = new OAuthService();
        try {
            OAuthSession stored = PersistentOAuthSession.getByState(state);
            AuthorizationCodeRequest request = new AuthorizationCodeRequest(code, stored.getCodeVerifier());
            if (oauthType.equals(OAuthService.PREAUTH)) {
                request.setJsonBody(false);
                request.setRedirectUri(request.getRedirectUri() + "oauth/" + OAuthService.PREAUTH + '/' + state);
            } else {
                request.setRedirectUri(request.getRedirectUri() + "oauth/" + OAuthService.SCA);
            }

            OAuthSession authorized = service.tokenRequest(stored.getTokenEndpoint(), request);
            authorized.setState(state);
            PersistentOAuthSession.update(authorized);
            LOG.info("Successfully received callback from ASPSP for OAuthSession state={}", stored.getState());
            return true;
        } catch (Exception e) {
            LOG.error(e);
            return false;
        }
    }

    private String getTppLink() {
        return Config.getInstance().getProperties().getProperty("client.redirect.baseurl");
    }

    private Response returnHTMLPage() {
        try (InputStream input = CallbackHandler.class.getClassLoader().getResourceAsStream("index.html")) {
            if (input == null) {
                LOG.warn("index.html for callback display cannot be located in the jar, returning plain/text");
                throw new FileNotFoundException();
            }
            Optional<String> o1 = Optional.of(IOUtils.toString(input, StandardCharsets.UTF_8.toString()));
            String linkToRedirect = this.getTppLink();
            String htmlContent = o1.get().replace("scaLink", linkToRedirect);
            return Response.status(Response.Status.TEMPORARY_REDIRECT).entity(htmlContent).build();
        } catch (Exception e) {
            return Response.status(200).entity("Thank you for using styx. In order to proceed, please use this link: " + this.getTppLink()).build();
        }
    }

    private Response returnHTMLErrorPage() {
        try (InputStream input = CallbackHandler.class.getClassLoader().getResourceAsStream("sca-error.html")) {
            if (input == null) {
                LOG.warn("sca-error.html for callback display cannot be located in the jar, returning plain/text");
                throw new FileNotFoundException();
            }
            String htmlContent = IOUtils.toString(input, StandardCharsets.UTF_8.toString());
            return Response.status(200).entity(htmlContent).build();
        } catch (Exception e) {
            return Response.status(200).entity(
                    "Bei der Authorisierung ist ein Fehler aufgetreten. Bitte versuchen Sie " +
                    "es zu einem späteren Zeitpunkt noch einmal."
            ).build();
        }
    }
}
