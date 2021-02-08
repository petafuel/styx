package net.petafuel.styx.core.xs2a.sca;

import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import net.petafuel.styx.core.xs2a.entities.Links;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.entities.StrongAuthenticatableResource;
import net.petafuel.styx.core.xs2a.exceptions.InvalidSCAMethodException;
import net.petafuel.styx.core.xs2a.oauth.OAuthService;
import net.petafuel.styx.core.xs2a.oauth.entities.OAuthSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class SCAHandler {

    private static final Logger LOG = LogManager.getLogger(SCAHandler.class);
    private SCAHandler() {
    }

    public static SCAApproach decision(StrongAuthenticatableResource strongAuthenticatableResource) {

        SCA sca;
        SCAApproach scaMethod = null;
        String scope;
        if (strongAuthenticatableResource instanceof Consent) {
            Consent consent = (Consent) strongAuthenticatableResource;
            sca = consent.getSca();
            scope = "AIS: " + consent.getId();
        } else if (strongAuthenticatableResource instanceof InitiatedPayment) {
            InitiatedPayment payment = (InitiatedPayment) strongAuthenticatableResource;
            sca = payment.getSca();
            scope = "PIS: " + payment.getPaymentId();
        } else {
            return null;
        }
        switch (sca.getApproach()) {
            case DECOUPLED:
                break;
            case EMBEDDED:
                break;
            case OAUTH2:
                String link;
                if (isLinkBuilt(strongAuthenticatableResource.getLinks().getScaOAuth())) {
                    link = strongAuthenticatableResource.getLinks().getScaOAuth().getUrl();
                } else {
                    OAuthSession session = OAuthService.startSession(strongAuthenticatableResource, scope);
                    link = OAuthService.buildLink(session.getState());
                }
                scaMethod = new OAuth2(link);
                break;
            case REDIRECT:
                scaMethod = new Redirect(strongAuthenticatableResource.getLinks().getScaRedirect().getUrl());
                break;
            case REQUIRE_AUTHORISATION_RESOURCE:
                //Do nothing
                break;
            default:
                throw new InvalidSCAMethodException("Found SCA Method is unsupported");
        }
        return scaMethod;
    }

    private static boolean isLinkBuilt(Links.Href scaOauthLink) {
        return scaOauthLink != null && getQueryParameterValue(scaOauthLink.getUrl(), "state") != null;
    }

    public static String getQueryParameterValue(String url, String key) {
        int i = url.indexOf('?');
        Map<String, String> paramsMap = new HashMap<>();
        if (i > -1) {
            String searchURL = url.substring(url.indexOf('?') + 1);
            String[] params = searchURL.split("&");

            for (String param : params) {
                String[] temp = param.split("=");
                try {
                    paramsMap.put(temp[0], URLDecoder.decode(temp[1], "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    LOG.warn("Query param was unable to be retrieved from the given link. param: {} url: {} message: {}", key, url, e.getMessage());
                }
            }
        }

        return paramsMap.get(key);
    }
}
