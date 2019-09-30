package net.petafuel.styx.core.xs2a.utils;

import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.SCA;

public class SCAHandler {

    public static void decision(Consent consent) {
        if (consent.getSca().getApproach() == SCA.Approach.REDIRECT) {
            consent.getAccess();

        } else {

        }
    }
}
