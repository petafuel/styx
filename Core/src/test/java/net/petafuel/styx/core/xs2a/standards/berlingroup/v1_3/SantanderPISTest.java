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
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.entities.PeriodicPayment;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.BerlinGroupSigner;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PaymentInitiationPain001Request;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PeriodicPaymentInitiationJsonRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PeriodicPaymentInitiationXMLRequest;
import net.petafuel.styx.core.xs2a.utils.jsepa.PmtInf;
import org.junit.Assert;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class SantanderPISTest {

    private static final String API_SANTANDER_DE_SCB_OPENAPIS_SX = "https://apigateway-sandbox.api.santander.de/scb-openapis/sx/";

    @Test
    @Tag("integration")
    public void initiateJsonPeriodicPayment() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setPis(new BerlinGroupPIS(API_SANTANDER_DE_SCB_OPENAPIS_SX, new BerlinGroupSigner()));

        //payment information
        String creditorIban = "DE15500105172295759744"; //Consorsbank
        Currency creditorCurrency = Currency.EUR;
        String creditorName = "WBG";
        String debtorIban = "DE60760300800500123456"; //Consorsbank
        Currency debtorCurrency = Currency.EUR;
        String amount = "520.00";
        Currency instructedCurrency = Currency.EUR;
        String reference = "Ref. Number WBG-1222";
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
        paymentBody.setRequestedExecutionDate(null);
        paymentBody.setDayOfExecution(dayOfExecution);

        PSU psu = new PSU("PSU-Successful");
        psu.setIp("192.168.8.78");
        PeriodicPaymentInitiationJsonRequest request = new PeriodicPaymentInitiationJsonRequest(PaymentProduct.SEPA_CREDIT_TRANSFERS, paymentBody, psu);
        request.setTppRedirectPreferred(true);

        InitiatedPayment payment = standard.getPis().initiatePayment(request);
        Assert.assertNotNull(payment);
        Assert.assertEquals(InitiatedPayment.Status.RCVD, payment.getStatus());
    }

    @Test
    @Tag("integration")
    public void initiateXMLPeriodicPayment() throws BankRequestFailedException{
        XS2AStandard standard = new XS2AStandard();
        standard.setPis(new BerlinGroupPIS(API_SANTANDER_DE_SCB_OPENAPIS_SX, new BerlinGroupSigner()));

        // Necessary instances for creating a PAIN00100303Document
        PAIN00100303Document document = new PAIN00100303Document();
        CCTInitiation ccInitation = new CCTInitiation();
        GroupHeader groupHeader = new GroupHeader();
        Vector<PaymentInstructionInformation> pmtInfos = new Vector<>();
        // TODO
        // PmtInf.java is created for temporary usage, until JSEPA supports the added attibute (BtchBookg)
        // use PaymentInstructionInformation instances and delete PmtInf.java once JSEPA is released
        // PaymentInstructionInformation pii = new PaymentInstructionInformation();
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
        String debtorIban = "DE60760300800500123456";
        String debtorBic = "TESTDETT421";
        String chargeBearer = "SLEV";
        boolean batchBooking = true;

        double amount1 = 100.00;
        String endToEndID = "EndToEndId";
        String creditorName1 = "Hans Handbuch";
        String creditorIBAN1 = "DE98701204008538752000";
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

        PSU psu = new PSU("PSU-Successful");
        psu.setIp("192.168.8.78");
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
        Assert.assertEquals(InitiatedPayment.Status.RCVD, payment.getStatus());
    }
}
