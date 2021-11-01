package net.petafuel.styx.api.v1.sad.entity;

import javax.json.bind.annotation.JsonbProperty;

public class ASPSPResponse {
    @JsonbProperty("name")
    private String name;

    @JsonbProperty("active")
    private Boolean active;

    @JsonbProperty("prestepRequired")
    private Boolean prestepRequired;

    @JsonbProperty("multicurrencyAccountsSupported")
    private Boolean multicurrencyAccountsSupported;

    @JsonbProperty("scaApproaches")
    private AvailableSCAApproaches scaApproaches;

    @JsonbProperty("supportedService")
    private SupportedServices supportedServices;

    public ASPSPResponse() {
        // default constructor for json binding
    }

    public ASPSPResponse(String name, Boolean active, AvailableSCAApproaches scaApproaches, SupportedServices supportedServices, Boolean prestepRequired, Boolean multicurrencyAccountsSupported) {
        this.name = name;
        this.active = active;
        this.scaApproaches = scaApproaches;
        this.supportedServices = supportedServices;
        this.prestepRequired = prestepRequired;
        this.multicurrencyAccountsSupported = multicurrencyAccountsSupported;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public AvailableSCAApproaches getScaApproaches() {
        return scaApproaches;
    }

    public void setScaApproaches(AvailableSCAApproaches scaApproaches) {
        this.scaApproaches = scaApproaches;
    }

    public SupportedServices getSupportedServices() {
        return supportedServices;
    }

    public void setSupportedServices(SupportedServices supportedServices) {
        this.supportedServices = supportedServices;
    }

    public Boolean getPrestepRequired() {
        return prestepRequired;
    }

    public void setPrestepRequired(Boolean prestepRequired) {
        this.prestepRequired = prestepRequired;
    }

    public Boolean getMulticurrencyAccountsSupported() {
        return multicurrencyAccountsSupported;
    }

    public void setMulticurrencyAccountsSupported(Boolean multicurrencyAccountsSupported) {
        this.multicurrencyAccountsSupported = multicurrencyAccountsSupported;
    }
}
