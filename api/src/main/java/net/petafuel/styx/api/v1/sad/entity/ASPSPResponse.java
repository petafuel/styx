package net.petafuel.styx.api.v1.sad.entity;

import javax.json.bind.annotation.JsonbProperty;

public class ASPSPResponse {
    @JsonbProperty("name")
    private String name;

    @JsonbProperty("active")
    private boolean active;

    @JsonbProperty("prestepRequired")
    private boolean prestepRequired;

    @JsonbProperty("multicurrencyAccountsSupported")
    private boolean multicurrencyAccountsSupported;

    @JsonbProperty("scaApproaches")
    private AvailableSCAApproaches scaApproaches;

    @JsonbProperty("supportedServices")
    private SupportedServices supportedServices;

    public ASPSPResponse() {
        // default constructor for json binding
    }

    public ASPSPResponse(String name, boolean active, AvailableSCAApproaches scaApproaches, SupportedServices supportedServices, boolean prestepRequired, boolean multicurrencyAccountsSupported) {
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

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
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

    public boolean getPrestepRequired() {
        return prestepRequired;
    }

    public void setPrestepRequired(boolean prestepRequired) {
        this.prestepRequired = prestepRequired;
    }

    public boolean getMulticurrencyAccountsSupported() {
        return multicurrencyAccountsSupported;
    }

    public void setMulticurrencyAccountsSupported(boolean multicurrencyAccountsSupported) {
        this.multicurrencyAccountsSupported = multicurrencyAccountsSupported;
    }
}
