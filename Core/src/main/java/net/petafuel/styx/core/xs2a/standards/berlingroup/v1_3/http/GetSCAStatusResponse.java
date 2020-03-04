package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

public class GetSCAStatusResponse {
    private String scaStatus;

    /**
     * @deprecated //Default constructor for Jsonb
     */
    @Deprecated
    public GetSCAStatusResponse() {
        //Default constructor for Jsonb
    }

    public String getScaStatus() {
        return scaStatus;
    }

    public void setScaStatus(String scaStatus) {
        this.scaStatus = scaStatus;
    }
}
