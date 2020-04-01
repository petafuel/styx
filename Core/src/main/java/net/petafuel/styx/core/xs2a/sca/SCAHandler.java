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

    public static SCAApproach decision(StrongAuthenticatableResource object) {

        SCA sca;
        SCAApproach scaMethod = null;
        String scope;
        if (object instanceof Consent) {
            Consent consent = (Consent) object;
            sca = consent.getSca();
            scope = "AIS: " + consent.getId();
        } else if (object instanceof InitiatedPayment) {
            InitiatedPayment payment = (InitiatedPayment) object;
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
                OAuthSession session = OAuthService.startSession(sca, scope);
                String link = OAuthService.buildLink(session.getState());
                scaMethod = new OAuth2(link);
                break;
            case REDIRECT:
                scaMethod = new Redirect(sca.getLinks().get(SCA.LinkType.SCA_REDIRECT));
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
