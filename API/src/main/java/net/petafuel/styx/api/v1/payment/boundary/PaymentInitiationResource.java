package net.petafuel.styx.api.v1.payment.boundary;

import net.petafuel.jsepa.model.PAIN00100303Document;
import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.api.filter.AbstractTokenFilter;
import net.petafuel.styx.api.filter.AcceptsPreStepAuth;
import net.petafuel.styx.api.filter.CheckAccessToken;
import net.petafuel.styx.api.filter.RequiresBIC;
import net.petafuel.styx.api.filter.RequiresMandatoryHeader;
import net.petafuel.styx.api.filter.RequiresPSU;
import net.petafuel.styx.api.rest.RestResource;
import net.petafuel.styx.api.util.AspspUrlMapper;
import net.petafuel.styx.api.util.io.IOProcessor;
import net.petafuel.styx.api.util.io.contracts.IOInputContainerPIS;
import net.petafuel.styx.api.v1.payment.entity.BulkPaymentInitiation;
import net.petafuel.styx.api.v1.payment.entity.PaymentResponse;
import net.petafuel.styx.api.v1.payment.entity.PaymentTypeBean;
import net.petafuel.styx.api.v1.payment.entity.PeriodicPaymentInitiation;
import net.petafuel.styx.api.v1.payment.entity.SinglePaymentInitiation;
import net.petafuel.styx.core.persistence.layers.PersistentPayment;
import net.petafuel.styx.core.persistence.models.AccessToken;
import net.petafuel.styx.core.xs2a.contracts.XS2APaymentRequest;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.BulkPayment;
import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import net.petafuel.styx.core.xs2a.entities.Payment;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.entities.PeriodicPayment;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.sca.OAuth2;
import net.petafuel.styx.core.xs2a.sca.SCAApproach;
import net.petafuel.styx.core.xs2a.sca.SCAHandler;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.BulkPaymentInitiationJsonRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PaymentInitiationJsonRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PaymentInitiationPain001Request;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PeriodicPaymentInitiationJsonRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PeriodicPaymentInitiationXMLRequest;
import net.petafuel.styx.core.xs2a.utils.PaymentXMLSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/**
 * @documented https://confluence.petafuel.intern/display/TOOL/Styx+PIS+-+Interface+Definition#StyxPISInterfaceDefinition-PaymentInitiationRequest
 */
@Path("/v1")
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@Consumes({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@CheckAccessToken(allowedServices = {AccessToken.ServiceType.AISPIS, AccessToken.ServiceType.PIS})
@RequiresPSU
@RequiresBIC
public class PaymentInitiationResource extends RestResource {
    private static final Logger LOG = LogManager.getLogger(PaymentInitiationResource.class);

    /**
     * Initiate single or future dated payments
     *
     * @param paymentTypeBean   contains which payment product is used
     * @param singlePaymentBody contains the request body as parsed json
     * @return 201 if successful
     * @throws BankRequestFailedException in case the communication between styx and aspsp was not successful
     */
    @POST
    @Path("/payments/{paymentProduct}")
    @RequiresMandatoryHeader
    @AcceptsPreStepAuth
    public Response initiateSinglePayment(@BeanParam PaymentTypeBean paymentTypeBean,
                                          @Valid SinglePaymentInitiation singlePaymentBody) throws BankRequestFailedException {

        Optional<Payment> singlePayment = singlePaymentBody.getPayments().stream().findFirst();
        if (!singlePayment.isPresent()) {
            throw new StyxException(new ResponseEntity("No valid single payment object was found within the payments array", ResponseConstant.BAD_REQUEST, ResponseCategory.ERROR, ResponseOrigin.CLIENT));
        }
        Payment payment = singlePayment.get();
        IOInputContainerPIS ioInputContainerPIS = new IOInputContainerPIS(getXS2AStandard(), getPsu(), payment, PaymentService.PAYMENTS, paymentTypeBean.getPaymentProduct());
        IOProcessor ioProcessor = new IOProcessor(ioInputContainerPIS);

        if (!ioProcessor.getIoInputContainerPIS().getPaymentProduct().isXml()) {
            PaymentInitiationJsonRequest paymentInitiationJsonRequest = new PaymentInitiationJsonRequest(ioProcessor.getIoInputContainerPIS().getPaymentProduct(), (Payment) ioProcessor.getIoInputContainerPIS().getPayment(), ioProcessor.getIoInputContainerPIS().getPsu());
            paymentInitiationJsonRequest.getHeaders().putAll(getAdditionalHeaders());
            ioProcessor.getIoInputContainerPIS().setXs2ARequest(paymentInitiationJsonRequest);
        } else {
            //aspsp does not support json, use pain001.003
            PAIN00100303Document document = (new PaymentXMLSerializer()).serialize(UUID.randomUUID().toString(), (Payment) ioProcessor.getIoInputContainerPIS().getPayment());
            PaymentInitiationPain001Request paymentInitiationPain001Request = new PaymentInitiationPain001Request(ioProcessor.getIoInputContainerPIS().getPaymentProduct(), PaymentService.PAYMENTS, document, ioProcessor.getIoInputContainerPIS().getPsu());
            paymentInitiationPain001Request.getHeaders().putAll(getAdditionalHeaders());
            ioProcessor.getIoInputContainerPIS().setXs2ARequest(paymentInitiationPain001Request);
        }

        XS2APaymentRequest aspspRequest = (XS2APaymentRequest) ioProcessor.applyOptions();
        aspspRequest.setTppRedirectPreferred(getRedirectPreferred());

        InitiatedPayment initiatedPayment = getXS2AStandard().getPis().initiatePayment(aspspRequest);
        PaymentResponse paymentResponse = new PaymentResponse(initiatedPayment);
        SCAApproach approach = SCAHandler.decision(initiatedPayment);
        if (approach instanceof OAuth2) {
            paymentResponse.getLinks().getScaOAuth().setUrl(((OAuth2) approach).getAuthoriseLink());
        }

        LOG.info("Initiate single payment bic={} aspsp_name={} aspsp_id={} paymentId={}", getXS2AStandard().getAspsp().getBic(), getXS2AStandard().getAspsp().getName(), getXS2AStandard().getAspsp().getId(), paymentResponse.getPaymentId());

        AspspUrlMapper aspspUrlMapper = new AspspUrlMapper(PaymentService.PAYMENTS, paymentTypeBean.getPaymentProduct(), paymentResponse.getPaymentId(), null);
        paymentResponse.setLinks(aspspUrlMapper.map(paymentResponse.getLinks()));

        PersistentPayment.create(paymentResponse.getPaymentId(), (String) getContainerRequestContext().getProperty(AbstractTokenFilter.class.getName()), getXS2AStandard().getAspsp().getBic(), paymentResponse.getTransactionStatus());
        return Response.status(ResponseConstant.CREATED).entity(paymentResponse).build();
    }

    /**
     * Initiate multiple payments as bulk
     *
     * @param paymentTypeBean contains which payment product is used
     * @param bulkPaymentBody contains the request body as parsed json
     * @return 201 if successful
     * @throws BankRequestFailedException in case the communication between styx and aspsp was not successful
     */
    @POST
    @Path("/bulk-payments/{paymentProduct}")
    @RequiresMandatoryHeader
    @AcceptsPreStepAuth
    public Response initiateBulkPayment(
            @BeanParam PaymentTypeBean paymentTypeBean,
            @Valid BulkPaymentInitiation bulkPaymentBody) throws BankRequestFailedException {
        //Debtors should all be the same within the payments, we take one of them
        Optional<Payment> singlePayment = bulkPaymentBody.getPayments().stream().findAny();
        if (!singlePayment.isPresent()) {
            throw new StyxException(new ResponseEntity("No valid payment object was found within the bulk payments array", ResponseConstant.BAD_REQUEST, ResponseCategory.ERROR, ResponseOrigin.CLIENT));
        }
        Account debtor = bulkPaymentBody.getDebtorAccount();
        BulkPayment bulkPayment = new BulkPayment();
        bulkPayment.setBatchBookingPreferred(bulkPaymentBody.getBatchBookingPreferred());
        bulkPayment.setDebtorAccount(debtor);
        bulkPayment.setPayments(bulkPaymentBody.getPayments());
        bulkPayment.setRequestedExecutionDate(bulkPaymentBody.getRequestedExecutionDate());
        IOInputContainerPIS ioInputContainerPIS = new IOInputContainerPIS(getXS2AStandard(), getPsu(), bulkPayment, PaymentService.BULK_PAYMENTS, paymentTypeBean.getPaymentProduct());
        IOProcessor ioProcessor = new IOProcessor(ioInputContainerPIS);
        if (!ioProcessor.getIoInputContainerPIS().getPaymentProduct().isXml()) {
            BulkPaymentInitiationJsonRequest bulkPaymentInitiationJsonRequest = new BulkPaymentInitiationJsonRequest(ioProcessor.getIoInputContainerPIS().getPaymentProduct(), (BulkPayment) ioProcessor.getIoInputContainerPIS().getPayment(), ioProcessor.getIoInputContainerPIS().getPsu());
            bulkPaymentInitiationJsonRequest.getHeaders().putAll(getAdditionalHeaders());
            ioProcessor.getIoInputContainerPIS().setXs2ARequest(bulkPaymentInitiationJsonRequest);
        } else {
            PAIN00100303Document document = (new PaymentXMLSerializer()).serialize(UUID.randomUUID().toString(), (BulkPayment) ioProcessor.getIoInputContainerPIS().getPayment());
            PaymentInitiationPain001Request paymentInitiationPain001Request = new PaymentInitiationPain001Request(ioProcessor.getIoInputContainerPIS().getPaymentProduct(), PaymentService.BULK_PAYMENTS, document, ioProcessor.getIoInputContainerPIS().getPsu());
            paymentInitiationPain001Request.getHeaders().putAll(getAdditionalHeaders());
            ioProcessor.getIoInputContainerPIS().setXs2ARequest(paymentInitiationPain001Request);
        }
        XS2APaymentRequest aspspRequest = (XS2APaymentRequest) ioProcessor.applyOptions();
        aspspRequest.setTppRedirectPreferred(getRedirectPreferred());

        InitiatedPayment initiatedPayment = getXS2AStandard().getPis().initiatePayment(aspspRequest);
        PaymentResponse paymentResponse = new PaymentResponse(initiatedPayment);
        SCAApproach approach = SCAHandler.decision(initiatedPayment);
        if (approach instanceof OAuth2) {
            paymentResponse.getLinks().getScaOAuth().setUrl(((OAuth2) approach).getAuthoriseLink());
        }
        LOG.info("Initiate bulk payment bic={} aspsp_name={} aspsp_id={} paymentId={}", getXS2AStandard().getAspsp().getBic(), getXS2AStandard().getAspsp().getName(), getXS2AStandard().getAspsp().getId(), paymentResponse.getPaymentId());

        AspspUrlMapper aspspUrlMapper = new AspspUrlMapper(PaymentService.BULK_PAYMENTS, paymentTypeBean.getPaymentProduct(), paymentResponse.getPaymentId(), null);
        paymentResponse.setLinks(aspspUrlMapper.map(paymentResponse.getLinks()));

        PersistentPayment.create(paymentResponse.getPaymentId(), (String) getContainerRequestContext().getProperty(AbstractTokenFilter.class.getName()), getXS2AStandard().getAspsp().getBic(), paymentResponse.getTransactionStatus());
        return Response.status(ResponseConstant.CREATED).entity(paymentResponse).build();
    }

    /**
     * Initiate a reoccurring payment
     *
     * @param paymentTypeBean     contains which payment product is used
     * @param periodicPaymentBody contains the request body as parsed json
     * @return 201 if successful
     * @throws BankRequestFailedException in case the communication between styx and aspsp was not successful
     */
    @POST
    @Path("/periodic-payments/{paymentProduct}")
    @RequiresMandatoryHeader
    @AcceptsPreStepAuth
    public Response initiatePeriodicPayment(@BeanParam PaymentTypeBean paymentTypeBean,
                                            @Valid PeriodicPaymentInitiation periodicPaymentBody) throws BankRequestFailedException {
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
        IOInputContainerPIS ioInputContainerPIS = new IOInputContainerPIS(getXS2AStandard(), getPsu(), periodicPayment, PaymentService.PERIODIC_PAYMENTS, paymentTypeBean.getPaymentProduct());
        IOProcessor ioProcessor = new IOProcessor(ioInputContainerPIS);

        if (!ioProcessor.getIoInputContainerPIS().getPaymentProduct().isXml()) {
            PeriodicPaymentInitiationJsonRequest periodicPaymentInitiationJsonRequest = new PeriodicPaymentInitiationJsonRequest(ioProcessor.getIoInputContainerPIS().getPaymentProduct(), (PeriodicPayment) ioProcessor.getIoInputContainerPIS().getPayment(), ioProcessor.getIoInputContainerPIS().getPsu());
            periodicPaymentInitiationJsonRequest.getHeaders().putAll(getAdditionalHeaders());
            ioProcessor.getIoInputContainerPIS().setXs2ARequest(periodicPaymentInitiationJsonRequest);
        } else {
            PAIN00100303Document document = (new PaymentXMLSerializer()).serialize(UUID.randomUUID().toString(), (PeriodicPayment) ioProcessor.getIoInputContainerPIS().getPayment());
            PeriodicPaymentInitiationXMLRequest periodicPaymentInitiationXMLRequest = new PeriodicPaymentInitiationXMLRequest(
                    new PaymentInitiationPain001Request(
                            ioProcessor.getIoInputContainerPIS().getPaymentProduct(),
                            PaymentService.PERIODIC_PAYMENTS,
                            document,
                            ioProcessor.getIoInputContainerPIS().getPsu()),
                    (PeriodicPayment) ioProcessor.getIoInputContainerPIS().getPayment());
            periodicPaymentInitiationXMLRequest.getHeaders().putAll(getAdditionalHeaders());
            ioProcessor.getIoInputContainerPIS().setXs2ARequest(periodicPaymentInitiationXMLRequest);
        }

        XS2APaymentRequest aspspRequest = (XS2APaymentRequest) ioProcessor.applyOptions();
        aspspRequest.setTppRedirectPreferred(getRedirectPreferred());

        InitiatedPayment initiatedPayment = getXS2AStandard().getPis().initiatePayment(aspspRequest);
        PaymentResponse paymentResponse = new PaymentResponse(initiatedPayment);
        SCAApproach approach = SCAHandler.decision(initiatedPayment);
        if (approach instanceof OAuth2) {
            paymentResponse.getLinks().getScaOAuth().setUrl(((OAuth2) approach).getAuthoriseLink());
        }
        LOG.info("Initiate periodic payment bic={} aspsp_name={} aspsp_id={}", getXS2AStandard().getAspsp().getBic(), getXS2AStandard().getAspsp().getName(), getXS2AStandard().getAspsp().getId());

        AspspUrlMapper aspspUrlMapper = new AspspUrlMapper(PaymentService.PERIODIC_PAYMENTS, paymentTypeBean.getPaymentProduct(), paymentResponse.getPaymentId(), null);
        paymentResponse.setLinks(aspspUrlMapper.map(paymentResponse.getLinks()));

        PersistentPayment.create(paymentResponse.getPaymentId(), (String) getContainerRequestContext().getProperty(AbstractTokenFilter.class.getName()), getXS2AStandard().getAspsp().getBic(), paymentResponse.getTransactionStatus());
        return Response.status(ResponseConstant.CREATED).entity(paymentResponse).build();
    }
}
