package net.petafuel.styx.api.v1.payment.control;

import net.petafuel.styx.api.v1.payment.entity.PaymentTypeBean;
import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.xs2a.entities.PSU;

import java.util.HashMap;
import java.util.Map;

public class PaymentProvider {
    protected final XS2AStandard xs2AStandard;
    protected final PaymentTypeBean paymentTypeBean;
    protected final PSU psu;
    protected final Map<String, String> additionalHeaders;

    public PaymentProvider(XS2AStandard xs2AStandard, PaymentTypeBean paymentTypeBean, PSU psu) {
        this.xs2AStandard = xs2AStandard;
        this.paymentTypeBean = paymentTypeBean;
        this.psu = psu;
        additionalHeaders = new HashMap<>();
    }

    public void addAdditionalHeaders(Map<String, String> additionalHeaders) {
        this.additionalHeaders.putAll(additionalHeaders);
    }
}
