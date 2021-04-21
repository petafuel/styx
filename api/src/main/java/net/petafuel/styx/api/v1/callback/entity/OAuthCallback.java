package net.petafuel.styx.api.v1.callback.entity;

import javax.ws.rs.QueryParam;

public class OAuthCallback {
    private String code;
    private String state;
    private String error;
    private String errorDescription;

    public String getCode() {
        return code;
    }

    @QueryParam("code")
    public void setCode(String code) {
        this.code = code;
    }

    public String getState() {
        return state;
    }

    @QueryParam("state")
    public void setState(String state) {
        this.state = state;
    }

    public String getError() {
        return error;
    }

    @QueryParam("error")
    public void setError(String error) {
        this.error = error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    @QueryParam("error_description")
    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    /**
     * Human readable format of an OAuthCallback
     *
     * @return string for logging
     */
    @Override
    public String toString() {
        return "OAuthCallback{" +
                "code='" + code + '\'' +
                ", state='" + state + '\'' +
                ", error='" + error + '\'' +
                ", errorDescription='" + errorDescription + '\'' +
                '}';
    }
}
