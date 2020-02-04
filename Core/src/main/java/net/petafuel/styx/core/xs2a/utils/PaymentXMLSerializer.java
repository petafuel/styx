package net.petafuel.styx.core.xs2a.utils;

import net.petafuel.jsepa.model.CCTInitiation;
import net.petafuel.jsepa.model.CreditTransferTransactionInformation;
import net.petafuel.jsepa.model.GroupHeader;
import net.petafuel.jsepa.model.PAIN00100303Document;
import net.petafuel.jsepa.model.PaymentInstructionInformation;
import net.petafuel.styx.core.xs2a.entities.BulkPayment;
import net.petafuel.styx.core.xs2a.entities.Payment;
import net.petafuel.styx.core.xs2a.utils.jsepa.PmtInf;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

public class PaymentXMLSerializer {
    private PAIN00100303Document document;
    private CCTInitiation ccInitation;
    private GroupHeader groupHeader;
    private Vector<PaymentInstructionInformation> pmtInfos;
    private PmtInf pii;
    private CreditTransferTransactionInformation cdtTrfTxInf;

    public PaymentXMLSerializer() {
        document = new PAIN00100303Document();
        ccInitation = new CCTInitiation();
        groupHeader = new GroupHeader();
        pmtInfos = new Vector<>();
        pii = new PmtInf();
        cdtTrfTxInf = new CreditTransferTransactionInformation();

    }

    public PAIN00100303Document serialize(String messageId, Payment payment) {

        // Necessary variables for creating a PAIN00100303Document
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String creationTime = simpleDateFormat.format(new Date());
        double controlSum = 0d;

        String paymentInformationId = "NOTPROVIDED";
        String paymentMethod = "TRF";
        String debtorName = payment.getDebtor().getName();
        String chargeBearer = "SLEV";

        // Setting values for each instance
        groupHeader.setMessageId(messageId);
        groupHeader.setCreationTime(creationTime);
        groupHeader.setNoOfTransactions(1);

        groupHeader.setInitiatingPartyName(debtorName);

        ArrayList<CreditTransferTransactionInformation> list = new ArrayList<>();

        controlSum += Double.parseDouble(payment.getInstructedAmount().getAmount());
        cdtTrfTxInf.setEndToEndID(payment.getEndToEndIdentification());
        cdtTrfTxInf.setAmount(Double.parseDouble(payment.getInstructedAmount().getAmount()));
        cdtTrfTxInf.setCreditorName(payment.getCreditorName());
        cdtTrfTxInf.setCreditorIBAN(payment.getCreditor().getIban());
        cdtTrfTxInf.setVwz(payment.getRemittanceInformationUnstructured());
        list.add(cdtTrfTxInf);

        groupHeader.setControlSum(controlSum);

        pii.setPmtInfId(paymentInformationId);
        pii.setPaymentMethod(paymentMethod);
        pii.setNoTxns(1);
        pii.setCtrlSum(controlSum);
        pii.setDebtorName(debtorName);
        pii.setDebtorAccountIBAN(payment.getDebtor().getIban());
        pii.setChargeBearer(chargeBearer);
        pii.setCreditTransferTransactionInformationVector(list);

        pmtInfos.add(pii);
        ccInitation.setGrpHeader(groupHeader);
        ccInitation.setPmtInfos(pmtInfos);
        document.setCctInitiation(ccInitation);
        return document;
    }

    public PAIN00100303Document serialize(String messageId, BulkPayment bulkPayment) {

        // Necessary variables for creating a PAIN00100303Document
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String creationTime = simpleDateFormat.format(new Date());
        double controlSum = 0d;

        String paymentInformationId = "NOTPROVIDED";
        String paymentMethod = "TRF";
        String debtorName = bulkPayment.getDebtorAccount().getName();
        String chargeBearer = "SLEV";

        // Setting values for each instance
        groupHeader.setMessageId(messageId);
        groupHeader.setCreationTime(creationTime);
        groupHeader.setNoOfTransactions(bulkPayment.getPayments().size());

        groupHeader.setInitiatingPartyName(debtorName);

        ArrayList<CreditTransferTransactionInformation> list = new ArrayList<>();
        for (Payment payment : bulkPayment.getPayments()) {
            controlSum += Double.parseDouble(payment.getInstructedAmount().getAmount());
            cdtTrfTxInf.setEndToEndID(payment.getEndToEndIdentification());
            cdtTrfTxInf.setAmount(Double.parseDouble(payment.getInstructedAmount().getAmount()));
            cdtTrfTxInf.setCreditorName(payment.getCreditorName());
            cdtTrfTxInf.setCreditorIBAN(payment.getCreditor().getIban());
            cdtTrfTxInf.setVwz(payment.getRemittanceInformationUnstructured());
            list.add(cdtTrfTxInf);
        }
        groupHeader.setControlSum(controlSum);

        pii.setPmtInfId(paymentInformationId);
        pii.setPaymentMethod(paymentMethod);
        pii.setNoTxns(bulkPayment.getPayments().size());
        pii.setCtrlSum(controlSum);
        pii.setDebtorName(debtorName);
        pii.setDebtorAccountIBAN(bulkPayment.getDebtorAccount().getIban());
        pii.setChargeBearer(chargeBearer);
        pii.setCreditTransferTransactionInformationVector(list);

        pmtInfos.add(pii);
        ccInitation.setGrpHeader(groupHeader);
        ccInitation.setPmtInfos(pmtInfos);
        document.setCctInitiation(ccInitation);
        return document;
    }
}
