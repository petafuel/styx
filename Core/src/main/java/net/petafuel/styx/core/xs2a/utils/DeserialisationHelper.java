package net.petafuel.styx.core.xs2a.utils;

import com.google.gson.JsonObject;
import net.petafuel.styx.core.xs2a.entities.SCA;

public class DeserialisationHelper {

    private DeserialisationHelper() {
    }

    /**
     * The SCA Object gets modified by reference, the sca approach will be parsed as well as the links map
     *
     * @param sca   this sca Object will be modified by this function
     * @param links a jsonObject which contains the _links object and underlying href -> url key-value pairs
     */
    public static void parseSCALinksData(SCA sca, JsonObject links) {
        if (links.get(SCA.LinkType.SCA_REDIRECT.getJsonKey()) != null) {
            sca.setApproach(SCA.Approach.REDIRECT);
        } else if (links.get(SCA.LinkType.SCA_OAUTH.getJsonKey()) != null) {
            sca.setApproach(SCA.Approach.OAUTH2);
        }
        for (SCA.LinkType linkType : SCA.LinkType.values()) {
            if (links.get(linkType.getJsonKey()) != null) {
                sca.addLink(linkType, links.get(linkType.getJsonKey()).getAsJsonObject().get("href").getAsString());
            }
        }
    }
}
