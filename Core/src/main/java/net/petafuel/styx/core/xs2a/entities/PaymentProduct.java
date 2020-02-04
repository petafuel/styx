package net.petafuel.styx.core.xs2a.entities;

import java.util.Arrays;

public enum PaymentProduct {

    SEPA_CREDIT_TRANSFERS("sepa-credit-transfers", false),
    INSTANT_SEPA_CREDIT_TRANSFERS("instant-sepa-credit-transfers", false),
    TARGET_2_PAYMENTS("target-2-payments", false),
    CROSS_BORDER_CREDIT_TRANSFERS("cross-border-credit-transfers", false),
    PAIN_001_SEPA_CREDIT_TRANSFERS("pain.001-sepa-credit-transfers", true),
    PAIN_001_INSTANT_SEPA_CREDIT_TRANSFERS("pain.001-instant-sepa-credit-transfers", true),
    PAIN_001_TARGET_2_PAYMENTS("pain.001-target-2-payments", true),
    PAIN_001_CROSS_BORDER_CREDIT_TRANSFERS("pain.001-cross-border-credit-transfers", true);

    private String value;
    private boolean xml;

    PaymentProduct(String value, boolean xml) {
        this.value = value;
        this.xml = xml;
    }

    @Override
    public String toString() {
        return value;
    }

    public boolean isXml() {
        return xml;
    }

    public static PaymentProduct byValue(final String paymentProductStr) {
        return Arrays.asList(PaymentProduct.values()).parallelStream().filter(paymentProduct -> paymentProduct.toString().equals(paymentProductStr)).findFirst().orElse(null);
    }
}
