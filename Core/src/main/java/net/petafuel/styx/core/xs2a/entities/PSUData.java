package net.petafuel.styx.core.xs2a.entities;

import javax.json.bind.annotation.JsonbProperty;

/**
 * Contains sensitive PSU data that is used whenever a Response is steering in the direction of authenticating
 * an Authorisation Resource like a consent or a payment
 * <p>
 * The usage for this model revolves around the Update PSU Data (AUTHENTICATION) for Decoupled or Embedded SCA Approach
 * <br>This model is relevant if a preceding requestbody contained one of the following keys:
 * <br>* updatePsuAuthentication
 * <br>* updateEncryptedPsuAuthentication
 * <br>* updateAdditionalPsuAuthentication
 * <br>* updateAdditionalEncryptedPsuAuthentication
 * </p>
 */
public class PSUData {
    /**
     * password in plaintext
     * conditional
     */
    @JsonbProperty("password")
    private String password;

    /**
     * encrypted password
     * conditional
     */
    @JsonbProperty("encryptedPassword")
    private String encryptedPassword;

    /**
     * Additional password in plaintext
     * conditional
     */
    @JsonbProperty("additionalPassword")
    private String additionalPassword;

    /**
     * If an additional encrypted password is required
     */
    @JsonbProperty("additionalEncryptedPassword")
    private String additionalEncryptedPassword;

    public PSUData(){

    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getAdditionalPassword() {
        return additionalPassword;
    }

    public void setAdditionalPassword(String additionalPassword) {
        this.additionalPassword = additionalPassword;
    }

    public String getAdditionalEncryptedPassword() {
        return additionalEncryptedPassword;
    }

    public void setAdditionalEncryptedPassword(String additionalEncryptedPassword) {
        this.additionalEncryptedPassword = additionalEncryptedPassword;
    }
}
