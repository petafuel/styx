package net.petafuel.styx.api.util.io.contracts;

import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.xs2a.contracts.XS2APaymentRequest;
import net.petafuel.styx.core.xs2a.entities.InitializablePayment;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;

/**
 * Contains Data for IOProcessor to create a request
 */
public class IOInputContainerPIS extends IOInputContainer {
    public static final String XML_PAYMENT_PRODUCT_PREFIX = "pain.001-";
    private InitializablePayment payment;
    private String paymentId;
    private PaymentService paymentService;
    private PaymentProduct paymentProduct;

    public IOInputContainerPIS(XS2AStandard xs2AStandard, PSU psu, InitializablePayment payment, PaymentService paymentService, PaymentProduct paymentProduct) {
        super(xs2AStandard, psu);
        this.payment = payment;
        this.paymentService = paymentService;
        this.paymentProduct = paymentProduct;
    }

    /**
     * initiate Payment
     *
     * @param xs2AStandard   current xs2aStandard
     * @param psu            PSU data for request headers
     * @param payment        a payment object to be set within the request object
     * @param paymentService current paymentService
     * @param paymentProduct current PaymentProduct
     */
    public IOInputContainerPIS(XS2AStandard xs2AStandard, PSU psu, InitializablePayment payment, PaymentService paymentService, PaymentProduct paymentProduct, XS2APaymentRequest xs2APaymentRequest) {
        super(xs2AStandard, psu);
        this.payment = payment;
        this.paymentService = paymentService;
        this.paymentProduct = paymentProduct;
        xs2ARequest = xs2APaymentRequest;
    }

    public IOInputContainerPIS(XS2AStandard xs2AStandard, PSU psu, String paymentId, PaymentService paymentService, PaymentProduct paymentProduct, XS2APaymentRequest xs2APaymentRequest) {
        super(xs2AStandard, psu);
        this.paymentId = paymentId;
        this.paymentService = paymentService;
        this.paymentProduct = paymentProduct;
        xs2ARequest = xs2APaymentRequest;
    }

    public IOInputContainerPIS(XS2AStandard xs2AStandard, PSU psu, String paymentId, PaymentService paymentService, PaymentProduct paymentProduct) {
        super(xs2AStandard, psu);
        this.paymentId = paymentId;
        this.paymentService = paymentService;
        this.paymentProduct = paymentProduct;
    }

    public InitializablePayment getPayment() {
        return payment;
    }

    public void setPayment(InitializablePayment payment) {
        this.payment = payment;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public PaymentProduct getPaymentProduct() {
        return paymentProduct;
    }

    public void setPaymentProduct(PaymentProduct paymentProduct) {
        this.paymentProduct = paymentProduct;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
}
