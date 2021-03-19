package net.petafuel.styx.core.xs2a.utils;

import net.petafuel.jsepa.model.CCTInitiation;
import net.petafuel.jsepa.model.CreditTransferTransactionInformation;
import net.petafuel.jsepa.model.GroupHeader;
import net.petafuel.jsepa.model.PAIN00100303Document;
import net.petafuel.jsepa.model.PaymentInstructionInformation;
import net.petafuel.styx.core.xs2a.entities.BulkPayment;
import net.petafuel.styx.core.xs2a.entities.SinglePayment;
import net.petafuel.styx.core.xs2a.utils.sepa.PmtInf;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.Vector;

public class PaymentXMLSerializer {
    private static final String NOT_PROVIDED = "NOTPROVIDED";
    private final PAIN00100303Document document;
    private final CCTInitiation ccInitation;
    private final GroupHeader groupHeader;
    //Vector is required by JSEPA Dependency
    @SuppressWarnings("squid:S1149")
    private final Vector<PaymentInstructionInformation> pmtInfos;
    private final PmtInf pii;
    private final SimpleDateFormat requestedExecutionDateFormat;
    private final SimpleDateFormat creationTimeFormat;

    public PaymentXMLSerializer() {
        document = new PAIN00100303Document();
        ccInitation = new CCTInitiation();
        groupHeader = new GroupHeader();
        pmtInfos = new Vector<>();
        pii = new PmtInf();
        requestedExecutionDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        creationTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    }

    public PAIN00100303Document serialize(String messageId, SinglePayment payment) {

        // Necessary variables for creating a PAIN00100303Document
        Date creationTime = new Date();
        double controlSum = 0d;

        String paymentMethod = "TRF";
        String debtorName = payment.getDebtorAccount().getName();
        String chargeBearer = "SLEV";

        // Setting values for each instance
        groupHeader.setMessageId(messageId.substring(0, Math.min(messageId.length(), 35)));
        groupHeader.setCreationTime(creationTimeFormat.format(creationTime));
        groupHeader.setNoOfTransactions(1);

        groupHeader.setInitiatingPartyName(debtorName);

        ArrayList<CreditTransferTransactionInformation> list = new ArrayList<>();
        CreditTransferTransactionInformation cdtTrfTxInf = new CreditTransferTransactionInformation();
        controlSum += Double.parseDouble(payment.getInstructedAmount().getAmount());
        cdtTrfTxInf.setEndToEndID(payment.getEndToEndIdentification() != null ? payment.getEndToEndIdentification() : UUID.randomUUID().toString());
        cdtTrfTxInf.setAmount(Double.parseDouble(payment.getInstructedAmount().getAmount()));
        cdtTrfTxInf.setCreditorName(payment.getCreditorName());
        cdtTrfTxInf.setCreditorIBAN(payment.getCreditorAccount().getIban());
        cdtTrfTxInf.setVwz(payment.getRemittanceInformationUnstructured());
        list.add(cdtTrfTxInf);

        groupHeader.setControlSum(controlSum);

        pii.setPmtInfId(NOT_PROVIDED);
        pii.setPaymentMethod(paymentMethod);
        pii.setNoTxns(1);
        pii.setCtrlSum(controlSum);
        pii.setDebtorName(debtorName);
        pii.setDebtorAccountIBAN(payment.getDebtorAccount().getIban());
        pii.setChargeBearer(chargeBearer);
        pii.setCreditTransferTransactionInformationVector(list);

        if (payment.getRequestedExecutionDate() == null) {
            pii.setRequestedExecutionDate(requestedExecutionDateFormat.format(creationTime));
        } else {
            pii.setRequestedExecutionDate(requestedExecutionDateFormat.format(payment.getRequestedExecutionDate()));
        }

        pmtInfos.add(pii);
        ccInitation.setGrpHeader(groupHeader);
        ccInitation.setPmtInfos(pmtInfos);
        document.setCctInitiation(ccInitation);
        return document;
    }

    public PAIN00100303Document serialize(String messageId, BulkPayment bulkPayment) {

        // Necessary variables for creating a PAIN00100303Document
        Date creationTime = new Date();
        double controlSum = 0d;

        String paymentMethod = "TRF";
        String chargeBearer = "SLEV";

        // Setting values for each instance
        groupHeader.setMessageId(messageId.substring(0, Math.min(messageId.length(), 35)));
        groupHeader.setCreationTime(creationTimeFormat.format(creationTime));
        groupHeader.setNoOfTransactions(bulkPayment.getPayments().size());

        groupHeader.setInitiatingPartyName(NOT_PROVIDED);

        ArrayList<CreditTransferTransactionInformation> list = new ArrayList<>();
        for (SinglePayment payment : bulkPayment.getPayments()) {
            CreditTransferTransactionInformation cdtTrfTxInf = new CreditTransferTransactionInformation();
            controlSum += Double.parseDouble(payment.getInstructedAmount().getAmount());
            cdtTrfTxInf.setEndToEndID(payment.getEndToEndIdentification() != null ? payment.getEndToEndIdentification() : UUID.randomUUID().toString());
            cdtTrfTxInf.setAmount(Double.parseDouble(payment.getInstructedAmount().getAmount()));
            cdtTrfTxInf.setCreditorName(payment.getCreditorName());
            cdtTrfTxInf.setCreditorIBAN(payment.getCreditorAccount().getIban());
            cdtTrfTxInf.setVwz(payment.getRemittanceInformationUnstructured());
            cdtTrfTxInf.setCreditorAgent(payment.getCreditorName());
            list.add(cdtTrfTxInf);
        }
        groupHeader.setControlSum(controlSum);

        pii.setPmtInfId(NOT_PROVIDED);
        pii.setPaymentMethod(paymentMethod);
        pii.setNoTxns(bulkPayment.getPayments().size());
        pii.setCtrlSum(controlSum);
        pii.setDebtorName(NOT_PROVIDED);
        pii.setDebtorAccountIBAN(bulkPayment.getDebtorAccount().getIban());
        pii.setChargeBearer(chargeBearer);
        pii.setBatchBooking(bulkPayment.getBatchBookingPreferred());
        pii.setCreditTransferTransactionInformationVector(list);

        if (bulkPayment.getRequestedExecutionDate() == null) {
            pii.setRequestedExecutionDate(requestedExecutionDateFormat.format(creationTime));
        } else {
            pii.setRequestedExecutionDate(requestedExecutionDateFormat.format(bulkPayment.getRequestedExecutionDate()));
        }

        pmtInfos.add(pii);
        ccInitation.setGrpHeader(groupHeader);
        ccInitation.setPmtInfos(pmtInfos);
        document.setCctInitiation(ccInitation);
        return document;
    }
}
