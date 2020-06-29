package net.petafuel.styx.core.xs2a.sca;

import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.entities.StrongAuthenticatableResource;
import net.petafuel.styx.core.xs2a.exceptions.InvalidSCAMethodException;
import net.petafuel.styx.core.xs2a.oauth.OAuthService;
import net.petafuel.styx.core.xs2a.oauth.entities.OAuthSession;

public class SCAHandler {

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
                OAuthSession session = OAuthService.startSession(strongAuthenticatableResource, scope);
                String link = OAuthService.buildLink(session.getState());
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
}
