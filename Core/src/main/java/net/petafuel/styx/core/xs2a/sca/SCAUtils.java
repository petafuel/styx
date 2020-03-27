package net.petafuel.styx.core.xs2a.sca;

import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.entities.Links;
import net.petafuel.styx.core.xs2a.entities.SCA;
import okhttp3.Response;

public class SCAUtils {

    private SCAUtils() {
    }

    public static void parseSCAApproach(SCA sca, Response response) {
        if (sca.getApproach() == null) {
            if (response.header(XS2AHeader.ASPSP_SCA_APPROACH) != null) {
                sca.setApproach(SCA.Approach.valueOf(response.header(XS2AHeader.ASPSP_SCA_APPROACH)));
            } else {
                sca.setApproach(SCA.Approach.REQUIRE_AUTHORISATION_RESOURCE);
            }
        }
    }

    public static SCA.Approach parseSCAApproach(Links links, Response response) {
        if (links.getScaOAuth() != null) {
            return SCA.Approach.OAUTH2;
        } else if (links.getScaRedirect() != null) {
            return SCA.Approach.REDIRECT;
        } else {
            if (response.header(XS2AHeader.ASPSP_SCA_APPROACH) != null) {
                return SCA.Approach.valueOf(response.header(XS2AHeader.ASPSP_SCA_APPROACH));
            } else {
                return SCA.Approach.REQUIRE_AUTHORISATION_RESOURCE;
            }
        }
    }
}
