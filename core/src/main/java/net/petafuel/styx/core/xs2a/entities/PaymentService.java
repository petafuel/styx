package net.petafuel.styx.core.xs2a.entities;

import java.util.Arrays;

public enum PaymentService {
    PAYMENTS("payments"),
    BULK_PAYMENTS("bulk-payments"),
    PERIODIC_PAYMENTS("periodic-payments");

    private final String name;

    PaymentService(String name) {
        this.name = name;
    }

    public String getValue() {
        return name;
    }

    public static PaymentService byValue(final String paymentServiceStr) {
        return Arrays.asList(PaymentService.values()).parallelStream().filter(paymentService -> paymentService.getValue().equals(paymentServiceStr)).findFirst().orElse(null);
    }
}
