package net.petafuel.styx.core.xs2a.sca;

import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.InvalidSCAMethodException;

public class SCAHandler {

    public static SCAApproach decision(Consent consent) {
        SCAApproach scaMethod = null;
        switch (consent.getSca().getApproach()) {
            case DECOUPLED:
                break;
            case EMBEDDED:
                break;
            case OAUTH2:
                break;
            case REDIRECT:
                scaMethod = new Redirect(consent.getSca().getLinks().get(SCA.LinkType.SCA_REDIRECT));
                break;
            default:
                throw new InvalidSCAMethodException("Found SCA Method is unsupported");
        }
        return scaMethod;
    }
}
