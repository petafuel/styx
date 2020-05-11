package net.petafuel.styx.api.v1.payment.control;

import net.petafuel.styx.api.util.io.IOProcessor;
import net.petafuel.styx.api.util.io.contracts.IOInputContainerPIS;
import net.petafuel.styx.api.v1.payment.entity.PaymentTypeBean;
import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.ReadPaymentRequest;

public class FetchPaymentProvider extends PaymentProvider {
    public FetchPaymentProvider(XS2AStandard xs2AStandard, PaymentTypeBean paymentTypeBean, PSU psu) {
        super(xs2AStandard, paymentTypeBean, psu);
    }

    public ReadPaymentRequest buildFetchPaymentRequest(String paymentId) {
        IOInputContainerPIS ioInputContainerPIS = new IOInputContainerPIS(IOInputContainerPIS.RequestType.FETCH, xs2AStandard, psu, paymentId, paymentTypeBean.getPaymentService(), paymentTypeBean.getPaymentProduct());
        ioInputContainerPIS.setAdditionalHeaders(additionalHeaders);
        IOProcessor ioProcessor = new IOProcessor(ioInputContainerPIS);
        return (ReadPaymentRequest) ioProcessor.applyOptions();
    }
}
