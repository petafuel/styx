package net.petafuel.styx.core.xs2a.entities;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import java.util.ArrayList;
import java.util.List;

public class SCA extends StrongAuthenticatableResource implements XS2AResponse{

    private Approach approach;
    private Status scaStatus;
    private String authorisationId;
    private List<AuthenticationObject> scaMethods;
    private AuthenticationObject chosenScaMethod;
    private Challenge challengeData;

    public SCA() {
        scaMethods = new ArrayList<>();
    }

    @JsonbCreator
    public SCA(@JsonbProperty("scaStatus") String scaStatus){
        this();
        this.scaStatus = Status.valueOf(scaStatus.toUpperCase());
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

    public AuthenticationObject getChosenScaMethod() {
        return chosenScaMethod;
    }

    public void setChosenScaMethod(AuthenticationObject chosenScaMethod) {
        this.chosenScaMethod = chosenScaMethod;
    }

    public Approach getApproach() {
        return approach;
    }

    public void setApproach(Approach approach) {
        this.approach = approach;
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

        private final String jsonKey;

        Status(String jsonKey) {
            this.jsonKey = jsonKey;
        }

        public String getValue() {
            return jsonKey;
        }
    }

}
