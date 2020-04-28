package net.petafuel.styx.api.v1.payment.control;

import net.petafuel.styx.api.util.io.IOProcessor;
import net.petafuel.styx.api.util.io.contracts.IOInputContainerPIS;
import net.petafuel.styx.api.v1.payment.entity.PaymentTypeBean;
import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.persistence.layers.PersistentPayment;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.TransactionStatus;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.ReadPaymentStatusRequest;

public class PaymentStatusProvider extends PaymentProvider {
    public PaymentStatusProvider(XS2AStandard xs2AStandard, PaymentTypeBean paymentTypeBean, PSU psu) {
        super(xs2AStandard, paymentTypeBean, psu);
    }

    public ReadPaymentStatusRequest buildRequest(String paymentId) {
        IOInputContainerPIS ioInputContainerPIS = new IOInputContainerPIS(IOInputContainerPIS.RequestType.STATUS, xs2AStandard, psu, paymentId, paymentTypeBean.getPaymentService(), paymentTypeBean.getPaymentProduct());
        IOProcessor ioProcessor = new IOProcessor(ioInputContainerPIS);
        return (ReadPaymentStatusRequest) ioProcessor.applyOptions();
    }

    public void updateStatus(String paymentId, String clientToken, String bic, TransactionStatus status) {
        if (PersistentPayment.get(paymentId) == null) {
            PersistentPayment.create(paymentId, clientToken, bic, status);
        } else {
            PersistentPayment.updateStatus(paymentId, status);
        }
    }
}
