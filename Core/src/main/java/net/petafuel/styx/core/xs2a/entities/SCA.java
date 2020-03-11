package net.petafuel.styx.core.xs2a.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SCA {

    private Approach approach;
    private Status scaStatus;
    private String authorisationId;
    private List<AuthenticationObject> scaMethods;
    private AuthenticationObject chosenSCAMethod;
    private Challenge challengeData;
    //_links is predefined by the berlingroup spec and used for json binding
    @SuppressWarnings("squid:S00116")
    private Map<LinkType, String> _links;
    private String psuMessage;

    public SCA() {
        this._links = new EnumMap<>(LinkType.class);
        scaMethods = new ArrayList<>();
    }

    public Challenge getChallengeData() {
        return challengeData;
    }

    public void setChallengeData(Challenge challengeData) {
        this.challengeData = challengeData;
    }

    public void addScaMethod(AuthenticationObject scaMethod) {
        scaMethods.add(scaMethod);
    }

    public List<AuthenticationObject> getScaMethods() {
        return scaMethods;
    }

    public void setScaMethods(List<AuthenticationObject> scaMethods) {
        this.scaMethods = scaMethods;
    }

    public AuthenticationObject getChosenSCAMethod() {
        return chosenSCAMethod;
    }

    public void setChosenSCAMethod(AuthenticationObject chosenSCAMethod) {
        this.chosenSCAMethod = chosenSCAMethod;
    }

    public String getPsuMessage() {
        return psuMessage;
    }

    public void setPsuMessage(String psuMessage) {
        this.psuMessage = psuMessage;
    }

    public Approach getApproach() {
        return approach;
    }

    public void setApproach(Approach approach) {
        this.approach = approach;
    }

    public Map<LinkType, String> getLinks() {
        return _links;
    }

    public void setLinks(Map<LinkType, String> links) {
        this._links = links;
    }

    public void addLink(LinkType linkType, String href) {
        this._links.put(linkType, href);
    }

    public Status getScaStatus() {
        return scaStatus;
    }

    public void setScaStatus(Status scaStatus) {
        this.scaStatus = scaStatus;
    }

    public String getAuthorisationId() {
        return authorisationId;
    }

    public void setAuthorisationId(String authorisationId) {
        this.authorisationId = authorisationId;
    }

    public enum Approach {
        DECOUPLED,
        EMBEDDED,
        REDIRECT,
        OAUTH2,
        REQUIRE_AUTHORISATION_RESOURCE
    }

    public enum Status {
        RECEIVED("received"),
        PSUIDENTIFIED("psuIdentified"),
        PSUAUTHENTICATED("psuAuthenticated"),
        SCAMETHODSELECTED("scaMethodSelected"),
        STARTED("started"),
        FINALISED("finalised"),
        FAILED("failed"),
        EXEMPTED("exempted");

        private String jsonKey;

        Status(String jsonKey) {
            this.jsonKey = jsonKey;
        }

        public String getValue() {
            return jsonKey;
        }
    }

    public enum LinkType {
        // ASPSP requires to create a authorisation resource
        START_AUTHORISATION("startAuthorisation"),
        AUTHORISATION_WITH_PSU_IDENTIFICATION("startAuthorisationWithPsuIdentfication"),
        AUTHORISATION_WITH_PSU_AUTHENTICATION("startAuthorisationWithPsuAuthentication"),
        AUTHORISATION_WITH_ENCRYPTED_PSU_AUTHENTICATION("startAuthorisationWithEncryptedPsuAuthentication"),
        AUTHORISATION_WITH_METHOD_SELECTION("startAuthorisationWithAuthentciationMethodSelection"),
        UPDATE_PSU_IDENTIFICATION("updatePsuIdentification"),
        UPDATE_PSU_AUTHENTICATION("updatePsuAuthentication"),
        UPDATE_ENCRYPTED_PSU_AUTHENTICATION("updateEncryptedPsuAuthentication"),
        UPDATE_ADDITIONAL_PSU_AUTHENTICATION("updateAdditionalPsuAuthentication"),
        UPDATE_ENCRYPTED_ADDITIONAL_PSU_AUTHENTICATION("updateAdditionalEncryptedPsuAuthentication"),
        AUTHORISE_TRANSACTION("authoriseTransaction"),
        STATUS("status"),
        SELF("self"),
        SCA_STATUS("scaStatus"),
        SCA_REDIRECT("scaRedirect"),
        SCA_OAUTH("scaOAuth");

        private String jsonKey;

        LinkType(String jsonKey) {
            this.jsonKey = jsonKey;
        }

        public static LinkType getByString(String search) {
            return Arrays.stream(LinkType.values()).filter(linkType -> linkType.getValue().equals(search)).findFirst().orElse(null);
        }

        public String getValue() {
            return jsonKey;
        }
    }
}
