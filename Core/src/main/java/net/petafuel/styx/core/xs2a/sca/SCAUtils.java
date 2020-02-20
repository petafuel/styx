package net.petafuel.styx.core.xs2a.sca;

import net.petafuel.styx.core.xs2a.entities.SCA;
import okhttp3.Response;

public class SCAUtils {

    private SCAUtils() {
    }

    public static void parseSCAApproach(SCA sca, Response response) {
        if (sca.getApproach() == null) {
            if (response.header("ASPSP-SCA-Approach") != null) {
                sca.setApproach(SCA.Approach.valueOf(response.header("ASPSP-SCA-Approach")));
            } else {
                sca.setApproach(SCA.Approach.REQUIRE_AUTHORISATION_RESOURCE);
            }
        }
    }
}
