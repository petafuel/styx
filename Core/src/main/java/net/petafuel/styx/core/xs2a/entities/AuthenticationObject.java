package net.petafuel.styx.core.xs2a.entities;

public class AuthenticationObject {
    /**
     * Type of the authentication method
     * mandatory
     */
    private AuthenticationType authenticationType;

    /**
     * Version of the used AuthenticationType
     * optional
     */
    private String authenticationVersion;

    /**
     * Identification provided by the ASPSP for later usage
     * mandatory
     */
    private String authenticationMethodId;

    /**
     * This is a general name of the Authentication method
     * This could be configured by the PSU through an ASPSP frontend or
     * this could also be a description by the ASPSP
     * mandatory
     */
    private String name;

    /**
     * Detailed information about the SCA Method for the PSU
     * optional
     */
    private String explanation;

    public AuthenticationType getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(AuthenticationType authenticationType) {
        this.authenticationType = authenticationType;
    }

    public String getAuthenticationVersion() {
        return authenticationVersion;
    }

    public void setAuthenticationVersion(String authenticationVersion) {
        this.authenticationVersion = authenticationVersion;
    }

    public String getAuthenticationMethodId() {
        return authenticationMethodId;
    }

    public void setAuthenticationMethodId(String authenticationMethodId) {
        this.authenticationMethodId = authenticationMethodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
