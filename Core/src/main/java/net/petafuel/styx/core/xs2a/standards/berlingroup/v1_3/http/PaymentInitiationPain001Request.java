package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import net.petafuel.jsepa.SEPAWriter;
import net.petafuel.jsepa.exception.SEPAWriteException;
import net.petafuel.jsepa.model.GroupHeader;
import net.petafuel.jsepa.model.PAIN00100303Document;
import net.petafuel.jsepa.model.PaymentInstructionInformation;
import net.petafuel.styx.core.xs2a.XS2APaymentInitiationRequest;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;

public class PaymentInitiationPain001Request extends XS2APaymentInitiationRequest {

    /** Body */
    private PAIN00100303Document body;

    public PaymentInitiationPain001Request(PaymentProduct paymentProduct, PaymentService paymentService, PAIN00100303Document body, PSU psu) {
        super(paymentProduct, paymentService, psu);
        this.body = body;

        PaymentInstructionInformation payment = body.getCctInitiation().getPmtInfos().get(0);
        GroupHeader groupHeader = body.getCctInitiation().getGrpHeader();

        if (this.getPaymentProduct().equals(PaymentProduct.PAIN_001_SEPA_CREDIT_TRANSFERS)) {
            if (payment.getRequestedExecutionDate() == null || "".equals(payment.getRequestedExecutionDate())){
                payment.setRequestedExecutionDate(groupHeader.getCreationTime());
            }
        }
    }

    @Override
    public String getRawBody() {

        SEPAWriter writer = new SEPAWriter(body);
        try {
            return new String(writer.writeSEPA());
        } catch (SEPAWriteException exception) {
            return "";
        }
    }

    public PAIN00100303Document getBody() {
        return body;
    }

    public void setBody(PAIN00100303Document body) {
        this.body = body;
    }

}
