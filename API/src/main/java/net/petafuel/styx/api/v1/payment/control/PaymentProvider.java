package net.petafuel.styx.api.v1.payment.control;

import net.petafuel.styx.api.v1.payment.entity.PaymentTypeBean;
import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.xs2a.entities.PSU;

public class PaymentProvider {
    protected static final String XML_PAYMENT_PRODUCT_PREFIX = "pain.001-";
    protected XS2AStandard xs2AStandard;
    protected PaymentTypeBean paymentTypeBean;
    protected PSU psu;

    public PaymentProvider(XS2AStandard xs2AStandard, PaymentTypeBean paymentTypeBean, PSU psu) {
        this.xs2AStandard = xs2AStandard;
        this.paymentTypeBean = paymentTypeBean;
        this.psu = psu;
    }
}
