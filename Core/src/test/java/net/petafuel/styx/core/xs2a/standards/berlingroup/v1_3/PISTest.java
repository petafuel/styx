package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3;

import net.petafuel.jsepa.model.CCTInitiation;
import net.petafuel.jsepa.model.CreditTransferTransactionInformation;
import net.petafuel.jsepa.model.GroupHeader;
import net.petafuel.jsepa.model.PAIN00100303Document;
import net.petafuel.jsepa.model.PaymentInstructionInformation;
import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Currency;
import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.Payment;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.entities.PaymentStatus;
import net.petafuel.styx.core.xs2a.entities.PeriodicPayment;
import net.petafuel.styx.core.xs2a.entities.TransactionStatus;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.sca.OAuth2;
import net.petafuel.styx.core.xs2a.sca.SCAApproach;
import net.petafuel.styx.core.xs2a.sca.SCAHandler;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.BerlinGroupSigner;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.BulkPaymentInitiationJsonRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PaymentInitiationJsonRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PaymentInitiationPain001Request;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PeriodicPaymentInitiationJsonRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PeriodicPaymentInitiationXMLRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.ReadPaymentStatusRequest;
import net.petafuel.styx.core.xs2a.utils.jsepa.PmtInf;
import org.junit.Assert;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class PISTest {

    private static final String SPARKASSE_BASE_API = "https://xs2a-sandbox.f-i-apim.de:8444/fixs2a-env/xs2a-api/12345678";
    private static final String PAYMENT_ID = "681275b3-9dc3-41a0-addd-cfc4764519c4";
    private static final String FIDUCIA_GAD_BASE_API = "https://xs2a-test.fiduciagad.de/xs2a";
    private static final String FIDUCIA_PAYMENT_ID = "3631391318101910234***REMOVED***PA4960JJ";
    public static final String DEUTSCHE_BANK_BASE_API ="https://simulator-xs2a.db.com:443/sb/sandbox";
    public static final String FIDOR_BANK_BASE_API = "https://xs2a.sandbox.fidorsolutions.cloud";

    @Test
    @Tag("integration")
    public void getPaymentStatus() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setPis(new BerlinGroupPIS(SPARKASSE_BASE_API, new BerlinGroupSigner()));

        ReadPaymentStatusRequest r1 = new ReadPaymentStatusRequest(
                PaymentService.PAYMENTS,
                PaymentProduct.SEPA_CREDIT_TRANSFERS,
                PAYMENT_ID);

        PaymentStatus status = standard.getPis().getPaymentStatus(r1);
        Assert.assertEquals(TransactionStatus.RCVD, status.getTransactionStatus());
    }

    @Test
    @Tag("integration")
    public void getPaymentStatusXML() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setPis(new BerlinGroupPIS(FIDUCIA_GAD_BASE_API, new BerlinGroupSigner()));

        ReadPaymentStatusRequest r1 = new ReadPaymentStatusRequest(
                PaymentService.PAYMENTS,
                PaymentProduct.PAIN_001_SEPA_CREDIT_TRANSFERS,
                FIDUCIA_PAYMENT_ID);

        PaymentStatus status = standard.getPis().getPaymentStatus(r1);
        Assert.assertEquals(TransactionStatus.RCVD, status.getTransactionStatus());
    }

    @Test
    @Tag("integration")
    public void initiateJSONPayment() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setPis(new BerlinGroupPIS(SPARKASSE_BASE_API, new BerlinGroupSigner()));

        //payment information
        String creditorIban = "DE75999999990000001004"; //Sparkasse
        Currency creditorCurrency = Currency.EUR;
        String creditorName = "Max Creditor";
        String debtorIban = "DE86999999990000001000"; //Sparkasse
        Currency debtorCurrency = Currency.EUR;
        String amount = "0.99";
        Currency instructedCurrency = Currency.EUR;
        String reference = "Beispiel Verwendungszweck";

        Payment paymentBody = new Payment();
        Account creditor = new Account(creditorIban, creditorCurrency, Account.Type.IBAN);
        creditor.setName(creditorName);
        Account debtor = new Account(debtorIban, debtorCurrency, Account.Type.IBAN);
        paymentBody.setCreditor(creditor);
        paymentBody.setDebtor(debtor);
        paymentBody.setAmount(amount);
        paymentBody.setCurrency(instructedCurrency);
        paymentBody.setReference(reference);

        PSU psu = new PSU("PSU-1234");
        PaymentInitiationJsonRequest request = new PaymentInitiationJsonRequest(PaymentProduct.SEPA_CREDIT_TRANSFERS, paymentBody, psu);
        request.setTppRedirectPreferred(true);

        InitiatedPayment payment = standard.getPis().initiatePayment(request);
        SCAApproach approach = SCAHandler.decision(payment);
        Assert.assertNotNull(payment);
    }

    @Test
    @Tag("integration")
    public void initiateJSONFuturePayment() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setPis(new BerlinGroupPIS(SPARKASSE_BASE_API, new BerlinGroupSigner()));

        //payment information
        String creditorIban = "DE75999999990000001004"; //Sparkasse
        Currency creditorCurrency = Currency.EUR;
        String creditorName = "Max Creditor";
        String debtorIban = "DE86999999990000001000"; //Sparkasse
        Currency debtorCurrency = Currency.EUR;
        String amount = "0.99";
        Currency instructedCurrency = Currency.EUR;
        String reference = "Beispiel Verwendungszweck";

        Date executionDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(executionDate);
        c.add(Calendar.DATE, 7);
        executionDate = c.getTime();

        Payment paymentBody = new Payment();
        Account creditor = new Account(creditorIban, creditorCurrency, Account.Type.IBAN);
        creditor.setName(creditorName);
        Account debtor = new Account(debtorIban, debtorCurrency, Account.Type.IBAN);
        paymentBody.setCreditor(creditor);
        paymentBody.setDebtor(debtor);
        paymentBody.setAmount(amount);
        paymentBody.setCurrency(instructedCurrency);
        paymentBody.setReference(reference);
        paymentBody.setRequestedExecutionDate(executionDate);

        PSU psu = new PSU("PSU-1234");
        PaymentInitiationJsonRequest request = new PaymentInitiationJsonRequest(PaymentProduct.SEPA_CREDIT_TRANSFERS, paymentBody, psu);

        InitiatedPayment payment = standard.getPis().initiatePayment(request);
        Assert.assertNotNull(payment);

    }

    @Tag("integration")
    @Test
    public void initializeSingleFuturePayment() throws BankRequestFailedException {

        XS2AStandard standard = new XS2AStandard();
        standard.setPis(new BerlinGroupPIS(SPARKASSE_BASE_API, new BerlinGroupSigner()));

        // Necessary instances for creating a PAIN00100303Document
        PAIN00100303Document document = new PAIN00100303Document();
        CCTInitiation ccInitation = new CCTInitiation();
        GroupHeader groupHeader = new GroupHeader();
        Vector<PaymentInstructionInformation> pmtInfos = new Vector<>();
        PaymentInstructionInformation p1 = new PaymentInstructionInformation();
        CreditTransferTransactionInformation cdtTrfTxInf = new CreditTransferTransactionInformation();

        // Necessary variables for creating a PAIN00100303Document
        String messageId = "messageId";
        String creationTime = "2019-10-10";
        int numberOfTransactions = 1;
        double controlSum = 100.00;
        double amount = 100.00;
        String initiatingPartyName = "initiatingPartyName";
        String paymentInformationId = "NOTPROVIDED";
        String paymentMethod = "TRF";
        String requestedExecutionDate = "2019-10-10";
        String debtorName = "Debtor Name";
        String debtorIban = "DE86999999990000001000";
        String debtorBic = "TESTDETT421";
        String chargeBearer = "SLEV";
        String endToEndID = "EndToEndId";
        String creditorName = "Hans Handbuch";
        String creditorIBAN = "DE98999999990000009999";
        String purpose = "purpose string";

        // Setting values for each instance
        groupHeader.setMessageId(messageId);
        groupHeader.setCreationTime(creationTime);
        groupHeader.setNoOfTransactions(numberOfTransactions);
        groupHeader.setControlSum(controlSum);
        groupHeader.setInitiatingPartyName(initiatingPartyName);

        cdtTrfTxInf.setEndToEndID(endToEndID);
        cdtTrfTxInf.setAmount(amount);
        cdtTrfTxInf.setCreditorName(creditorName);
        cdtTrfTxInf.setCreditorIBAN(creditorIBAN);
        cdtTrfTxInf.setVwz(purpose);

        p1.setPmtInfId(paymentInformationId);
        p1.setPaymentMethod(paymentMethod);
        p1.setNoTxns(numberOfTransactions);
        p1.setCtrlSum(controlSum);
        p1.setRequestedExecutionDate(requestedExecutionDate);
        p1.setDebtorName(debtorName);
        p1.setDebtorAccountIBAN(debtorIban);
        p1.setDebitorBic(debtorBic);
        p1.setChargeBearer(chargeBearer);

        p1.setCreditTransferTransactionInformationVector(Collections.singletonList(cdtTrfTxInf));
        pmtInfos.add(p1);
        ccInitation.setGrpHeader(groupHeader);
        ccInitation.setPmtInfos(pmtInfos);
        document.setCctInitiation(ccInitation);

        // Creating the request instance
        String psuIpAddress = "192.168.1.1";

        PaymentInitiationPain001Request request = new PaymentInitiationPain001Request(
                PaymentProduct.PAIN_001_SEPA_CREDIT_TRANSFERS, PaymentService.PAYMENTS, document, new PSU("PSU-1234")
        );
        request.setTppRedirectPreferred(true);
        request.getPsu().setIp(psuIpAddress);

        InitiatedPayment payment = standard.getPis().initiatePayment(request);
        SCAApproach approach = SCAHandler.decision(payment);
        System.out.println(((OAuth2) approach).getAuthoriseLink());
        Assert.assertNotNull(payment);

    }


    @Test
    @Tag("integration")
    public void initiateJsonBulkPayment() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setPis(new BerlinGroupPIS(SPARKASSE_BASE_API, new BerlinGroupSigner()));

        /** Debtor information*/
        String debtorIban = "DE86999999990000001000"; //Sparkasse
        Currency debtorCurrency = Currency.EUR;
        Account debtor = new Account(debtorIban, debtorCurrency, Account.Type.IBAN);

        /** Payment 1 information*/
        String creditorIban1 = "DE75999999990000001004"; //Sparkasse
        Currency creditorCurrency1 = Currency.EUR;
        String creditorName1 = "Creditor One";
        String instructedAmount1 = "0.99";
        Currency instructedCurrency1 = Currency.EUR;
        String reference1 = "Beispiel Verwendungszweck 1";

        Account creditor1 = new Account(creditorIban1, creditorCurrency1, Account.Type.IBAN);
        creditor1.setName(creditorName1);

        Payment p1 = new Payment();

        p1.setDebtor(debtor);
        p1.setCreditor(creditor1);
        p1.setAmount(instructedAmount1);
        p1.setCurrency(instructedCurrency1);
        p1.setReference(reference1);
        p1.setEndToEndIdentification("RI-234567890");


        /** Payment 2 information*/
        String creditorIban2 = "DE12999999990000001002"; //Sparkasse
        Currency creditorCurrency2 = Currency.EUR;
        String creditorName2 = "Creditor Two";
        String instructedAmount2 = "1.50";
        Currency instructedCurrency2 = Currency.EUR;
        String reference2 = "Beispiel Verwendungszweck 2";

        Account creditor2 = new Account(creditorIban2, creditorCurrency2, Account.Type.IBAN);
        creditor2.setName(creditorName2);

        Payment p2 = new Payment();

        p2.setDebtor(debtor);
        p2.setCreditor(creditor2);
        p2.setAmount(instructedAmount2);
        p2.setCurrency(instructedCurrency2);
        p2.setReference(reference2);
        p2.setEndToEndIdentification("WBG-123456789");

        List<Payment> payments = new LinkedList<>();
        payments.add(p1);
        payments.add(p2);

        PSU psu = new PSU("PSU-1234");
        BulkPaymentInitiationJsonRequest request = new BulkPaymentInitiationJsonRequest(
                PaymentProduct.SEPA_CREDIT_TRANSFERS, payments, psu, false);

        InitiatedPayment initiatedPayment = standard.getPis().initiatePayment(request);
        Assert.assertNotNull(initiatedPayment);
    }

    @Test
    @Tag("integration")
    public void initializeXMLBulkPayment() throws BankRequestFailedException {

        XS2AStandard standard = new XS2AStandard();
        standard.setPis(new BerlinGroupPIS(SPARKASSE_BASE_API, new BerlinGroupSigner()));

        // Necessary instances for creating a PAIN00100303Document
        PAIN00100303Document document = new PAIN00100303Document();
        CCTInitiation ccInitation = new CCTInitiation();
        GroupHeader groupHeader = new GroupHeader();
        Vector<PaymentInstructionInformation> pmtInfos = new Vector<>();
        // TODO
        //  PmtInf.java is created for temporary usage, until JSEPA supports the added attibute (BtchBookg)
        //  use PaymentInstructionInformation instances and delete PmtInf.java once JSEPA is released
//        PaymentInstructionInformation pii = new PaymentInstructionInformation();
        PmtInf pii = new PmtInf();
        CreditTransferTransactionInformation cdtTrfTxInf1 = new CreditTransferTransactionInformation();
        CreditTransferTransactionInformation cdtTrfTxInf2 = new CreditTransferTransactionInformation();

        // Necessary variables for creating a PAIN00100303Document
        String messageId = "messageId";
        String creationTime = "2019-11-11";
        int numberOfTransactions = 2;
        double controlSum = 200.00;
        String initiatingPartyName = "initiatingPartyName";
        String paymentInformationId = "NOTPROVIDED";
        String paymentMethod = "TRF";
        String requestedExecutionDate = "2019-11-11";
        String debtorName = "Debtor Name";
        String debtorIban = "DE86999999990000001000";
        String debtorBic = "TESTDETT421";
        String chargeBearer = "SLEV";
        boolean batchBooking = true;

        // PAYMENT 1
        double amount1 = 100.00;
        String endToEndID1 = "EndToEndId";
        String creditorName1 = "Hans Handbuch";
        String creditorIBAN1 = "DE98999999990000009999";
        String purpose1 = "purpose string";
        String creditorAgent1 = "AGENT1";

        // PAYMENT 2
        double amount2 = 100.00;
        String endToEndID2 = "EndToEndId";
        String creditorName2 = "Hans Handbuch";
        String creditorIBAN2 = "DE98999999990000009999";
        String purpose2 = "purpose string";
        String creditorAgent2 = "AGENT2";

        // Setting values for each instance
        groupHeader.setMessageId(messageId);
        groupHeader.setCreationTime(creationTime);
        groupHeader.setNoOfTransactions(numberOfTransactions);
        groupHeader.setControlSum(controlSum);
        groupHeader.setInitiatingPartyName(initiatingPartyName);

        cdtTrfTxInf1.setEndToEndID(endToEndID1);
        cdtTrfTxInf1.setAmount(amount1);
        cdtTrfTxInf1.setCreditorName(creditorName1);
        cdtTrfTxInf1.setCreditorIBAN(creditorIBAN1);
        cdtTrfTxInf1.setVwz(purpose1);
        cdtTrfTxInf1.setCreditorAgent(creditorAgent1);

        cdtTrfTxInf2.setEndToEndID(endToEndID2);
        cdtTrfTxInf2.setAmount(amount2);
        cdtTrfTxInf2.setCreditorName(creditorName2);
        cdtTrfTxInf2.setCreditorIBAN(creditorIBAN2);
        cdtTrfTxInf2.setVwz(purpose2);
        cdtTrfTxInf2.setCreditorAgent(creditorAgent2);

        ArrayList<CreditTransferTransactionInformation> list = new ArrayList<>();
        list.add(cdtTrfTxInf1);
        list.add(cdtTrfTxInf2);

        pii.setPmtInfId(paymentInformationId);
        pii.setPaymentMethod(paymentMethod);
        pii.setNoTxns(numberOfTransactions);
        pii.setCtrlSum(controlSum);
        pii.setRequestedExecutionDate(requestedExecutionDate);
        pii.setDebtorName(debtorName);
        pii.setDebtorAccountIBAN(debtorIban);
        pii.setDebitorBic(debtorBic);
        pii.setChargeBearer(chargeBearer);
        pii.setBatchBooking(batchBooking);
        pii.setCreditTransferTransactionInformationVector(list);

        pmtInfos.add(pii);
        ccInitation.setGrpHeader(groupHeader);
        ccInitation.setPmtInfos(pmtInfos);
        document.setCctInitiation(ccInitation);

        PSU psu = new PSU("PSU-1234");

        // Creating the request instance
        PaymentInitiationPain001Request request = new PaymentInitiationPain001Request(
                PaymentProduct.PAIN_001_SEPA_CREDIT_TRANSFERS, PaymentService.BULK_PAYMENTS, document, psu
        );

        InitiatedPayment payment = standard.getPis().initiatePayment(request);
        Assert.assertNotNull(payment);
    }

    @Test
    @Tag("integration")
    public void initiateJsonPeriodicPayment() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setPis(new BerlinGroupPIS(SPARKASSE_BASE_API, new BerlinGroupSigner()));

        //payment information
        String creditorIban = "DE75999999990000001004"; //Sparkasse
        Currency creditorCurrency = Currency.EUR;
        String creditorName = "Max Creditor";
        String debtorIban = "DE86999999990000001000"; //Sparkasse
        Currency debtorCurrency = Currency.EUR;
        String amount = "0.99";
        Currency instructedCurrency = Currency.EUR;
        String reference = "Beispiel Verwendungszweck";
        //additional periodic payment information
        Calendar day = Calendar.getInstance();
        day.set(Calendar.DAY_OF_MONTH, day.getActualMinimum(Calendar.DAY_OF_MONTH));
        day.add(Calendar.MONTH, 1);
        Date startDate = day.getTime();
        day.add(Calendar.MONTH, 2);
        Date endDate = day.getTime();
        PeriodicPayment.ExecutionRule executionRule = PeriodicPayment.ExecutionRule.following;
        PeriodicPayment.Frequency frequency = PeriodicPayment.Frequency.MNTH;
        String dayOfExecution = "20";

        PeriodicPayment paymentBody = new PeriodicPayment(startDate, frequency);
        Account creditor = new Account(creditorIban, creditorCurrency, Account.Type.IBAN);
        creditor.setName(creditorName);
        Account debtor = new Account(debtorIban, debtorCurrency, Account.Type.IBAN);
        paymentBody.setCreditor(creditor);
        paymentBody.setDebtor(debtor);
        paymentBody.setAmount(amount);
        paymentBody.setCurrency(instructedCurrency);
        paymentBody.setReference(reference);
        paymentBody.setExecutionRule(executionRule);
        paymentBody.setEndDate(endDate);
        paymentBody.setDayOfExecution(dayOfExecution);

        PSU psu = new PSU("PSU-1234");
        PeriodicPaymentInitiationJsonRequest request = new PeriodicPaymentInitiationJsonRequest(PaymentProduct.SEPA_CREDIT_TRANSFERS, paymentBody, psu);
        request.setTppRedirectPreferred(true);

        InitiatedPayment payment = standard.getPis().initiatePayment(request);
        Assert.assertNotNull(payment);
        Assert.assertEquals(TransactionStatus.RCVD, payment.getStatus());
    }

    @Test
    @Tag("integration")
    public void initiateXMLPeriodicPayment() throws BankRequestFailedException{
        XS2AStandard standard = new XS2AStandard();
        standard.setPis(new BerlinGroupPIS(SPARKASSE_BASE_API, new BerlinGroupSigner()));

        // Necessary instances for creating a PAIN00100303Document
        PAIN00100303Document document = new PAIN00100303Document();
        CCTInitiation ccInitation = new CCTInitiation();
        GroupHeader groupHeader = new GroupHeader();
        Vector<PaymentInstructionInformation> pmtInfos = new Vector<>();
        // TODO
        //  PmtInf.java is created for temporary usage, until JSEPA supports the added attibute (BtchBookg)
        //  use PaymentInstructionInformation instances and delete PmtInf.java once JSEPA is released
//        PaymentInstructionInformation pii = new PaymentInstructionInformation();
        PmtInf pii = new PmtInf();
        CreditTransferTransactionInformation cdtTrfTxInf1 = new CreditTransferTransactionInformation();

        //build xml request part
        // Necessary variables for creating a PAIN00100303Document
        String messageId = "messageId";
        String creationTime = "2019-11-11";
        int numberOfTransactions = 2;
        double controlSum = 200.00;
        String initiatingPartyName = "initiatingPartyName";
        String paymentInformationId = "NOTPROVIDED";
        String paymentMethod = "TRF";
        String requestedExecutionDate = "2019-11-11";
        String debtorName = "Debtor Name";
        String debtorIban = "DE86999999990000001000";
        String debtorBic = "TESTDETT421";
        String chargeBearer = "SLEV";
        boolean batchBooking = true;

        double amount1 = 100.00;
        String endToEndID = "EndToEndId";
        String creditorName1 = "Hans Handbuch";
        String creditorIBAN1 = "DE98999999990000009999";
        String purpose1 = "purpose string";
        String creditorAgent1 = "AGENT1";

        //payment information
        //additional periodic payment information
        Calendar day = Calendar.getInstance();
        day.set(Calendar.DAY_OF_MONTH, day.getActualMinimum(Calendar.DAY_OF_MONTH));
        day.add(Calendar.MONTH, 1);
        Date startDate = day.getTime();
        day.add(Calendar.MONTH, 2);
        Date endDate = day.getTime();
        PeriodicPayment.ExecutionRule executionRule = PeriodicPayment.ExecutionRule.following;
        PeriodicPayment.Frequency frequency = PeriodicPayment.Frequency.MNTH;
        String dayOfExecution = "20";

        // Setting values for each instance
        groupHeader.setMessageId(messageId);
        groupHeader.setCreationTime(creationTime);
        groupHeader.setNoOfTransactions(numberOfTransactions);
        groupHeader.setControlSum(controlSum);
        groupHeader.setInitiatingPartyName(initiatingPartyName);

        cdtTrfTxInf1.setEndToEndID(endToEndID);
        cdtTrfTxInf1.setAmount(amount1);
        cdtTrfTxInf1.setCreditorName(creditorName1);
        cdtTrfTxInf1.setCreditorIBAN(creditorIBAN1);
        cdtTrfTxInf1.setVwz(purpose1);
        cdtTrfTxInf1.setCreditorAgent(creditorAgent1);

        ArrayList<CreditTransferTransactionInformation> list = new ArrayList<>();
        list.add(cdtTrfTxInf1);

        pii.setPmtInfId(paymentInformationId);
        pii.setPaymentMethod(paymentMethod);
        pii.setNoTxns(numberOfTransactions);
        pii.setCtrlSum(controlSum);
        pii.setRequestedExecutionDate(requestedExecutionDate);
        pii.setDebtorName(debtorName);
        pii.setDebtorAccountIBAN(debtorIban);
        pii.setDebitorBic(debtorBic);
        pii.setChargeBearer(chargeBearer);
        pii.setBatchBooking(batchBooking);
        pii.setCreditTransferTransactionInformationVector(list);

        pmtInfos.add(pii);
        ccInitation.setGrpHeader(groupHeader);
        ccInitation.setPmtInfos(pmtInfos);
        document.setCctInitiation(ccInitation);

        PSU psu = new PSU("PSU-1234");
        PaymentInitiationPain001Request xmlRequest = new PaymentInitiationPain001Request(
                PaymentProduct.PAIN_001_SEPA_CREDIT_TRANSFERS, PaymentService.PERIODIC_PAYMENTS, document, psu
        );
        xmlRequest.setTppRedirectPreferred(true);

        //build json request part
        PeriodicPayment paymentBody = new PeriodicPayment(startDate, frequency);
        paymentBody.setExecutionRule(executionRule);
        paymentBody.setEndDate(endDate);
        paymentBody.setDayOfExecution(dayOfExecution);

        //merge together xml and json into one request
        PeriodicPaymentInitiationXMLRequest request = new PeriodicPaymentInitiationXMLRequest(xmlRequest, paymentBody);

        InitiatedPayment payment = standard.getPis().initiatePayment(request);
        Assert.assertNotNull(payment);
        Assert.assertEquals(TransactionStatus.RCVD, payment.getStatus());
    }
}
