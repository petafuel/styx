package net.petafuel.styx.api.v1.authentication.entity;

import net.petafuel.styx.api.validator.ValidateIntAsString;
import net.petafuel.styx.api.validator.ValidateTokenType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.ws.rs.HeaderParam;

/**
 * Request Resource for access/client token creation
 */
public class ClientTokenInfo {
    @HeaderParam("token")
    @NotBlank(message = "token header cannot be blank")
    @NotNull(message = "token header cannot be null")
    private String token;

    @ValidateTokenType(message = "service type is invalid")
    @HeaderParam("service")
    @NotBlank(message = "service header cannot be blank")
    @NotNull(message = "service header cannot be null")
    private String service;

    @ValidateIntAsString(min = 0, message = "expiresIn cannot be higher than max integer range or unsigned integer")
    @HeaderParam("expiresIn")
    private String expiresIn;

    @HeaderParam("clientReference")
    private String clientReference;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Integer getExpiresIn() {
        return expiresIn != null ? Integer.parseInt(expiresIn) : null;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = String.valueOf(expiresIn);
    }

    public String getClientReference() {
        return clientReference;
    }
}
