package net.petafuel.styx.api.v1.status.control;

import net.petafuel.styx.api.util.ApiProperties;
import net.petafuel.styx.api.util.URIHelper;
import net.petafuel.styx.api.v1.status.entity.RedirectStatus;

import javax.ws.rs.core.Response;
import java.net.URI;

/**
 * Util class to easily create internal redirects onto styx status pages
 */
public class StatusHelper {
    private StatusHelper(){}

    public static Response createStatusRedirection(RedirectStatus status){
        String baseUrl;
        if (Boolean.parseBoolean(System.getProperty(ApiProperties.STYX_PROXY_ENABLED))) {
            baseUrl = URIHelper.getProxyStyxURI();
        } else {
            baseUrl = URIHelper.getLocalStyxURI();
        }
        baseUrl += "/v1/status/" + status.getStatusType().name().toLowerCase() + "/" + status.getIdentification();

        if(status.getRedirectStep() != null){
            baseUrl += "?step=" + status.getRedirectStep().name().toLowerCase();
        }
        return Response.temporaryRedirect(URI.create(baseUrl)).build();
    }
}
