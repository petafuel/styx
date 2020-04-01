package net.petafuel.styx.api.v1.payment.control;

import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.api.util.io.IOProcessor;
import net.petafuel.styx.api.util.io.contracts.IOInputContainerPIS;
import net.petafuel.styx.api.v1.payment.entity.BulkPaymentInitiation;
import net.petafuel.styx.api.v1.payment.entity.PaymentTypeBean;
import net.petafuel.styx.api.v1.payment.entity.PeriodicPaymentInitiation;
import net.petafuel.styx.api.v1.payment.entity.SinglePaymentInitiation;
import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.xs2a.XS2APaymentRequest;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.BulkPayment;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.Payment;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.entities.PeriodicPayment;

import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

public class PaymentInitiationProvider extends PaymentProvider {
    public PaymentInitiationProvider(XS2AStandard xs2AStandard, PaymentTypeBean paymentTypeBean, PSU psu) {
        super(xs2AStandard, paymentTypeBean, psu);
    }

    public XS2APaymentRequest buildSinglePaymentRequest(SinglePaymentInitiation singlePaymentBody) {
        Optional<Payment> singlePayment = singlePaymentBody.getPayments().stream().findFirst();
        if (!singlePayment.isPresent()) {
            throw new StyxException(new ResponseEntity("No valid single payment object was found within the payments array", ResponseConstant.BAD_REQUEST, ResponseCategory.ERROR, ResponseOrigin.CLIENT));
        }
        Payment payment = singlePayment.get();
        IOProcessor ioProcessor = new IOProcessor(new IOInputContainerPIS(IOInputContainerPIS.RequestType.INITIATE, xs2AStandard, psu, payment, PaymentService.PAYMENTS, paymentTypeBean.getPaymentProduct()));
        return (XS2APaymentRequest) ioProcessor.applyOptions();
    }

    public XS2APaymentRequest buildBulkPaymentRequest(BulkPaymentInitiation bulkPaymentBody) {
        //Debtors should all be the same within the payments, we take one of them
        Optional<Payment> singlePayment = bulkPaymentBody.getPayments().stream().findAny();
        if (!singlePayment.isPresent()) {
            throw new StyxException(new ResponseEntity("No valid payment object was found within the bulk payments array", ResponseConstant.BAD_REQUEST, ResponseCategory.ERROR, ResponseOrigin.CLIENT));
        }
        Account debtor = singlePayment.get().getDebtor();
        BulkPayment bulkPayment = new BulkPayment();
        bulkPayment.setBatchBookingPreferred(bulkPaymentBody.getBatchBookingPreferred());
        bulkPayment.setDebtorAccount(debtor);
        bulkPayment.setPayments(bulkPaymentBody.getPayments());
        bulkPayment.setRequestedExecutionDate(bulkPaymentBody.getRequestedExecutionDate());

        IOProcessor ioProcessor = new IOProcessor(new IOInputContainerPIS(IOInputContainerPIS.RequestType.INITIATE, xs2AStandard, psu, bulkPayment, PaymentService.BULK_PAYMENTS, paymentTypeBean.getPaymentProduct()));
        return (XS2APaymentRequest) ioProcessor.applyOptions();
    }

    public XS2APaymentRequest buildPeriodicPaymentRequest(PeriodicPaymentInitiation periodicPaymentBody) {
        Optional<Payment> singlePayment = periodicPaymentBody.getPayments().stream().findFirst();
        if (!singlePayment.isPresent()) {
            throw new StyxException(new ResponseEntity("No valid payment object was found", ResponseConstant.BAD_REQUEST, ResponseCategory.ERROR, ResponseOrigin.CLIENT));
        }
        Payment payment = singlePayment.get();

        PeriodicPayment periodicPayment = new PeriodicPayment();
        periodicPayment.setCreditor(payment.getCreditor());
        periodicPayment.setCreditorName(payment.getCreditorName());
        periodicPayment.setDebtor(payment.getDebtor());
        periodicPayment.setEndToEndIdentification(payment.getEndToEndIdentification());
        periodicPayment.setInstructedAmount(payment.getInstructedAmount());
        periodicPayment.setRemittanceInformationUnstructured(payment.getRemittanceInformationUnstructured());
        periodicPayment.setDayOfExecution(String.valueOf(periodicPaymentBody.getDayOfExecution()));
        periodicPayment.setExecutionRule(periodicPaymentBody.getExecutionRule());
        periodicPayment.setStartDate(Date.from(periodicPaymentBody.getStartDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        periodicPayment.setFrequency(periodicPaymentBody.getFrequency().name());

        IOProcessor ioProcessor = new IOProcessor(new IOInputContainerPIS(IOInputContainerPIS.RequestType.INITIATE, xs2AStandard, psu, periodicPayment, PaymentService.PERIODIC_PAYMENTS, paymentTypeBean.getPaymentProduct()));
        return (XS2APaymentRequest) ioProcessor.applyOptions();
    }
}
