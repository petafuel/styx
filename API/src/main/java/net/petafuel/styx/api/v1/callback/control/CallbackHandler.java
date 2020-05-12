package net.petafuel.styx.api.v1.callback.control;

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
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class CallbackHandler {
    private static final Logger LOG = LogManager.getLogger(CallbackHandler.class);

    public Response handleRedirect(String xRequestId, HttpHeaders httpHeaders) {
        LOG.info("Handling callback request xrequsetid={}", xRequestId);
        StringBuilder output = new StringBuilder();
        for(String field : httpHeaders.getRequestHeaders().keySet()){
            output.append(" ").append(field).append(": ").append(httpHeaders.getRequestHeader(field)).append("\n");
        }
        LOG.info("requestHeader={}", output);

        return this.returnHTMLPage();
    }

    public Response handleOAuth2(String code, String state, String error, String errorMessage, String param) {
        String linkToRedirect;

        try {
            if (error == null && handleSuccessfulOAuth2(code, state, param)) {
                return this.returnHTMLPage();
            } else {
                linkToRedirect = handleFailedOAuth2(state);
                LOG.error("failed oauth2 callback error={}, errorMessage={}", error, errorMessage);
                return Response.temporaryRedirect(new URI(linkToRedirect)).build();
            }
        } catch (Exception e) {
            String message = "Handling callback with code: " + code + " and state: " + state;
            return Response.status(200).entity(message).build();
        }
    }

    private boolean handleSuccessfulOAuth2(String code, String state, String param) {
        OAuthService service = new OAuthService();
        PersistentOAuthSession db = new PersistentOAuthSession();
        OAuthSession stored = db.get(state);
        try {
            AuthorizationCodeRequest request = new AuthorizationCodeRequest(code, stored.getCodeVerifier());
            if (param.equals(OAuthService.PREAUTH)) {
                request.setJsonBody(false);
                request.setRedirectUri(request.getRedirectUri() + OAuthService.PREAUTH);
            }
            OAuthSession authorized = service.tokenRequest(stored.getTokenEndpoint(), request);
            authorized.setState(state);
            db.update(authorized);

            return true;
        }  catch (Exception e) {
            return false;
        }
    }

    private String handleFailedOAuth2(String state) {
        return OAuthService.buildLink(state);
    }

    private String getTppLink() {
        return Config.getInstance().getProperties().getProperty("client.redirect.baseurl");
    }

    private Response returnHTMLPage() {
        try (InputStream input = new FileInputStream("API/src/main/resources/index.html")) {
            Optional<String> o1 = Optional.of(IOUtils.toString(input, StandardCharsets.UTF_8.toString()));
            String linkToRedirect = this.getTppLink();
            String htmlContent = o1.get().replace("scaLink",  linkToRedirect);
            return Response.status(Response.Status.TEMPORARY_REDIRECT).entity(htmlContent).build();
        } catch (Exception e) {
            return Response.status(200).entity("hello world").build();
        }
    }
}
