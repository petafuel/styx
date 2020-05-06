package net.petafuel.styx.core.xs2a.entities;

import javax.json.bind.annotation.JsonbProperty;
import java.util.List;

/**
 * Container class which holds transactions/revenues grouped by the BookingType
 */
public class AccountReport {
    private List<Transaction> booked;
    private List<Transaction> pending;
    private List<Transaction> information;
    private Links links;

    public List<Transaction> getBooked() {
        return booked;
    }

    public void setBooked(List<Transaction> booked) {
        this.booked = booked;
    }

    public List<Transaction> getPending() {
        return pending;
    }

    public void setPending(List<Transaction> pending) {
        this.pending = pending;
    }

    public List<Transaction> getInformation() {
        return information;
    }

    public void setInformation(List<Transaction> information) {
        this.information = information;
    }

    @JsonbProperty("links")
    public Links getLinks() {
        return links;
    }

    @JsonbProperty("_links")
    public void setLinks(Links links) {
        this.links = links;
    }
}