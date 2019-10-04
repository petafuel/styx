package net.petafuel.styx.core.xs2a.entities;

import java.util.Arrays;
import java.util.EnumMap;

public class SCA {

    public enum Approach {
        DECOUPLED,
        EMBEDDED,
        REDIRECT,
        OAUTH2
    }

    public enum Status {
        RECEIVED,
        PSU_IDENTIFIED,
        PSU_AUTHENTICATED,
        SCA_METHOD_SELECTED,
        STARTED,
        FINALISED,
        FAILED,
        EXEMPTED
    }

    public enum LinkType {

        START_AUTHORISATION("startAuthorisation"),
        AUTHORISATION_WITH_PSU_IDENTIFICATION("startAuthorisationWithPsuIdentification"),
        AUTHORISATION_WITH_PSU_AUTHENTICATION("startAuthorisationWithPsuAuthentication"),
        AUTHORISATION_WITH_ENCRYPTED_PSU_AUTHENTICATION("startAuthorisationWithEncryptedPsuAuthentication"),
        STATUS("status"),
        SELF("self"),
        SCA_REDIRECT("scaRedirect"),
        SCA_OAUTH("scaOAuth");

        private String jsonKey;

        LinkType(String jsonKey) {
            this.jsonKey = jsonKey;
        }

        public String getJsonKey() {
            return jsonKey;
        }

        public static LinkType getByString(String search) {
            return Arrays.stream(LinkType.values()).filter(linkType -> linkType.getJsonKey().equals(search)).findFirst().orElse(null);
        }
    }

    public SCA() {
        this._links = new EnumMap<>(LinkType.class);
    }

    private Approach approach;

    private EnumMap<LinkType, String> _links;

    public Approach getApproach() {
        return approach;
    }

    public void setApproach(Approach approach) {
        this.approach = approach;
    }

    public EnumMap<LinkType, String> getLinks() {
        return _links;
    }

    public void setLinks(EnumMap<LinkType, String> links) {
        this._links = links;
    }

    public void addLink(LinkType linkType, String href) {
        this._links.put(linkType, href);
    }
}
