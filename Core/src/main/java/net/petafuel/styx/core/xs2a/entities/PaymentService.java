package net.petafuel.styx.core.xs2a.entities;

public enum PaymentService {
    PAYMENTS("payments"),
    BULK_PAYMENTS("bulk-payments"),
    PERIODIC_PAYMENTS("periodic-payments");

    private String name;
    PaymentService(String name) {
        this.name = name;
    }

    public String getValue() {
        return name;
    }
}
