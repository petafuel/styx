package net.petafuel.styx.api.v1.sad.entity;

import javax.json.bind.annotation.JsonbProperty;

public class SupportedServices {
    @JsonbProperty("pis")
    private SupportedServicesPIS pis;

    @JsonbProperty("ais")
    private SupportedServicesAIS ais;

    @JsonbProperty("cof")
    private SupportedServicesCOF cof;

    public SupportedServices() {
        // default constructor for json b
    }

    public SupportedServices(SupportedServicesPIS pis, SupportedServicesAIS ais, SupportedServicesCOF cof) {
        this.pis = pis;
        this.ais = ais;
        this.cof = cof;
    }

    public SupportedServicesPIS getPis() {
        return pis;
    }

    public void setPis(SupportedServicesPIS pis) {
        this.pis = pis;
    }

    public SupportedServicesAIS getAis() {
        return ais;
    }

    public void setAis(SupportedServicesAIS ais) {
        this.ais = ais;
    }

    public SupportedServicesCOF getCof() {
        return cof;
    }

    public void setCof(SupportedServicesCOF cof) {
        this.cof = cof;
    }
}
