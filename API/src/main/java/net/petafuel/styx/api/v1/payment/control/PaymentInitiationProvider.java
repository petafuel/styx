package net.petafuel.styx.api.v1.payment.control;

import com.google.gson.JsonElement;
import net.petafuel.jsepa.model.PAIN00100303Document;
import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.api.util.IOParser;
import net.petafuel.styx.api.v1.payment.entity.BulkPaymentInitiation;
import net.petafuel.styx.api.v1.payment.entity.PaymentTypeBean;
import net.petafuel.styx.api.v1.payment.entity.PeriodicPaymentInitiation;
import net.petafuel.styx.api.v1.payment.entity.SinglePaymentInitiation;
import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.xs2a.XS2APaymentInitiationRequest;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.BulkPayment;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.Payment;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.entities.PeriodicPayment;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.BulkPaymentInitiationJsonRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PaymentInitiationJsonRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PaymentInitiationPain001Request;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PeriodicPaymentInitiationJsonRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PeriodicPaymentInitiationXMLRequest;
import net.petafuel.styx.core.xs2a.utils.PaymentXMLSerializer;

import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public class PaymentInitiationProvider extends PaymentProvider {

    public PaymentInitiationProvider(XS2AStandard xs2AStandard, PaymentTypeBean paymentTypeBean, PSU psu) {
        super(xs2AStandard, paymentTypeBean, psu);
    }

    public XS2APaymentInitiationRequest buildSinglePaymentRequest(SinglePaymentInitiation singlePaymentBody) {
        Optional<Payment> singlePayment = singlePaymentBody.getPayments().stream().findFirst();
        if (!singlePayment.isPresent()) {
            throw new StyxException(new ResponseEntity("No valid single payment object was found within the payments array", ResponseConstant.BAD_REQUEST, ResponseCategory.ERROR, ResponseOrigin.CLIENT));
        }
        Payment payment = singlePayment.get();
        IOParser ioParser = new IOParser(xs2AStandard.getAspsp());

        //Check if payment is a future payment and if aspsp supports this
        if (payment.getRequestedExecutionDate() != null && !ioParser.getOption("IO21", IOParser.Option.AVAILABLE).getAsBoolean()) {
            throw new StyxException(new ResponseEntity("ASPSP does not support future-dated payments but requestedExecutionDate was set", ResponseConstant.BAD_REQUEST, ResponseCategory.ERROR, ResponseOrigin.ASPSP));
        }

        XS2APaymentInitiationRequest aspspRequest;
        //check IO2 for xml or json - single payment product
        if (ioParser.getOption("IO2", paymentTypeBean.getPaymentProduct().getValue()).getAsBoolean()) {
            //aspsp accepts json
            aspspRequest = new PaymentInitiationJsonRequest(paymentTypeBean.getPaymentProduct(), payment, psu);
        } else if (ioParser.getOption("IO2", XML_PAYMENT_PRODUCT_PREFIX + paymentTypeBean.getPaymentProduct().getValue()).getAsBoolean()) {
            //aspsp does not support json, use pain001.003
            PAIN00100303Document document = (new PaymentXMLSerializer()).serialize(UUID.randomUUID().toString(), payment);
            aspspRequest = new PaymentInitiationPain001Request(PaymentProduct.byValue(XML_PAYMENT_PRODUCT_PREFIX + paymentTypeBean.getPaymentProduct().getValue()), PaymentService.PAYMENTS, document, psu);
        } else {
            throw new StyxException(new ResponseEntity("The requested ASPSP does not support single-payments with payment-product " + paymentTypeBean.getPaymentProduct().getValue(), ResponseConstant.BAD_REQUEST, ResponseCategory.ERROR, ResponseOrigin.ASPSP));
        }

        return aspspRequest;
    }

    public XS2APaymentInitiationRequest buildBulkPaymentRequest(BulkPaymentInitiation bulkPaymentBody) {
        //Debitors should all be the same within the payments, we take one of them
        Optional<Payment> singlePayment = bulkPaymentBody.getPayments().stream().findAny();
        if (!singlePayment.isPresent()) {
            throw new StyxException(new ResponseEntity("No valid payment object was found within the bulk payments array", ResponseConstant.BAD_REQUEST, ResponseCategory.ERROR, ResponseOrigin.CLIENT));
        }
        IOParser ioParser = new IOParser(xs2AStandard.getAspsp());

        Account debtor = singlePayment.get().getDebtor();
        BulkPayment bulkPayment = new BulkPayment();
        bulkPayment.setBatchBookingPreferred(bulkPaymentBody.getBatchBookingPreferred());
        bulkPayment.setDebtorAccount(debtor);
        bulkPayment.setPayments(bulkPaymentBody.getPayments());
        bulkPayment.setRequestedExecutionDate(bulkPaymentBody.getRequestedExecutionDate());

        XS2APaymentInitiationRequest aspspRequest;

        //check IO3 for xml or json - bulk payment product
        if (ioParser.getOption("IO3", paymentTypeBean.getPaymentProduct().getValue()).getAsBoolean()) {
            aspspRequest = new BulkPaymentInitiationJsonRequest(paymentTypeBean.getPaymentProduct(), bulkPayment, psu);
        } else if (ioParser.getOption("IO3", XML_PAYMENT_PRODUCT_PREFIX + paymentTypeBean.getPaymentProduct().getValue()).getAsBoolean()) {
            PAIN00100303Document document = (new PaymentXMLSerializer()).serialize(UUID.randomUUID().toString(), bulkPayment);
            aspspRequest = new PaymentInitiationPain001Request(PaymentProduct.byValue(XML_PAYMENT_PRODUCT_PREFIX + paymentTypeBean.getPaymentProduct().getValue()), PaymentService.BULK_PAYMENTS, document, psu);
        } else {
            throw new StyxException(new ResponseEntity("The requested ASPSP does not support bulk-payments with payment-product " + paymentTypeBean.getPaymentProduct().getValue(), ResponseConstant.BAD_REQUEST, ResponseCategory.ERROR, ResponseOrigin.ASPSP));
        }
        return aspspRequest;
    }

    public XS2APaymentInitiationRequest buildPeriodicPaymentRequest(PeriodicPaymentInitiation periodicPaymentBody) {
        IOParser ioParser = new IOParser(xs2AStandard.getAspsp());
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

        //Check if bank uses frequency codes itself or frequency code names
        JsonElement aspspUsesFrequencyName = ioParser.getOption("STYX01", IOParser.Option.REQUIRED);
        if (aspspUsesFrequencyName != null && aspspUsesFrequencyName.getAsBoolean()) {
            periodicPayment.setFrequency(periodicPaymentBody.getFrequency().getValue());
        }
        XS2APaymentInitiationRequest aspspRequest;
        //check IO4 for xml or json - periodic payment product
        if (ioParser.getOption("IO4", paymentTypeBean.getPaymentProduct().getValue()).getAsBoolean()) {
            aspspRequest = new PeriodicPaymentInitiationJsonRequest(paymentTypeBean.getPaymentProduct(), periodicPayment, psu);
        } else if (ioParser.getOption("IO4", XML_PAYMENT_PRODUCT_PREFIX + paymentTypeBean.getPaymentProduct().getValue()).getAsBoolean()) {
            PAIN00100303Document document = (new PaymentXMLSerializer()).serialize(UUID.randomUUID().toString(), periodicPayment);
            aspspRequest = new PeriodicPaymentInitiationXMLRequest(
                    new PaymentInitiationPain001Request(
                            PaymentProduct.byValue(XML_PAYMENT_PRODUCT_PREFIX + paymentTypeBean.getPaymentProduct().getValue()),
                            PaymentService.PERIODIC_PAYMENTS,
                            document,
                            psu),
                    periodicPayment);
        } else {
            throw new StyxException(new ResponseEntity("The requested ASPSP does not support periodic-payments with payment-product " + paymentTypeBean.getPaymentProduct().getValue(), ResponseConstant.BAD_REQUEST, ResponseCategory.ERROR, ResponseOrigin.ASPSP));
        }
        return aspspRequest;
    }
}
