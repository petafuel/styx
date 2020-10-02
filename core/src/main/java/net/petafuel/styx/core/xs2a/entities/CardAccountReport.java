package net.petafuel.styx.core.xs2a.entities;

import javax.json.bind.annotation.JsonbProperty;
import java.util.List;

public class CardAccountReport {
    private List<CardTransaction> booked;
    private List<CardTransaction> pending;
    private Links links;

    public List<CardTransaction> getBooked() {
        return booked;
    }

    public void setBooked(List<CardTransaction> booked) {
        this.booked = booked;
    }

    public List<CardTransaction> getPending() {
        return pending;
    }

    public void setPending(List<CardTransaction> pending) {
        this.pending = pending;
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