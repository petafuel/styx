package net.petafuel.styx.api.v1.authentication.entity;

import net.petafuel.styx.api.validator.ValidateString;
import net.petafuel.styx.core.persistence.models.AccessToken;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.ws.rs.HeaderParam;

/**
 * Request Resource for access/client token creation
 */
public class ClientTokenInfo {
    @HeaderParam("token")
    @NotBlank(message = "token header cannot be blank")
    @NotNull(message = "token header cannot be null")
    private String token;

    @ValidateString(allowedValues = {"ais", "pis", "piis", "aispis"}, message = "service must be one of ais, pis, piis or aispis")
    @HeaderParam("service")
    @NotBlank(message = "service header cannot be blank")
    @NotNull(message = "service header cannot be null")
    private String service;

    @Max(value = Integer.MAX_VALUE, message = "expiresIn cannot be higher than max integer range")
    @PositiveOrZero(message = "expiresIn integer value cannot be unsigned")
    @HeaderParam("expiresIn")
    private Integer expiresIn;

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

    public AccessToken.ServiceType getServiceType() {
        return AccessToken.ServiceType.getByString(service);
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }
}
