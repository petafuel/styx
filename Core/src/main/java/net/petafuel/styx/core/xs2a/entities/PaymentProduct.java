package net.petafuel.styx.core.xs2a.entities;

public enum PaymentProduct {

    SEPA_CREDIT_TRANSFERS("pain.001-sepa-credit-transfers"),
    INSTANT_SEPA_CREDIT_TRANSFERS("instant-sepa-credit-transfers"),
    TARGET_2_PAYMENTS("target-2-payments"),
    CROSS_BORDER_CREDIT_TRANSFERS("cross-border-credit-transfers"),
    PAIN_001_SEPA_CREDIT_TRANSFERS("pain.001-sepa-credit-transfers"),
    PAIN_001_INSTANT_SEPA_CREDIT_TRANSFERS("pain.001-instant-sepa-credit-transfers"),
    PAIN_001_TARGET_2_PAYMENTS("pain.001-target-2-payments"),
    PAIN_001_CROSS_BORDER_CREDIT_TRANSFERS("pain.001-cross-border-credit-transfers");

    private String value;

    PaymentProduct(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
