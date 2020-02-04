package net.petafuel.styx.api.v1.payment.boundary;

import net.petafuel.jsepa.model.PAIN00100303Document;
import net.petafuel.styx.api.WebServer;
import net.petafuel.styx.api.exception.ErrorCategory;
import net.petafuel.styx.api.exception.ErrorEntity;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.api.filter.CheckAccessToken;
import net.petafuel.styx.api.filter.RequiresBIC;
import net.petafuel.styx.api.filter.RequiresPSU;
import net.petafuel.styx.api.rest.entity.PSUResource;
import net.petafuel.styx.api.v1.payment.entity.BulkPaymentInitiation;
import net.petafuel.styx.api.v1.payment.entity.PaymentInitiation;
import net.petafuel.styx.api.v1.payment.entity.PaymentProductBean;
import net.petafuel.styx.api.v1.payment.entity.PaymentResponse;
import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.banklookup.exceptions.BankLookupFailedException;
import net.petafuel.styx.core.banklookup.exceptions.BankNotFoundException;
import net.petafuel.styx.core.banklookup.sad.SAD;
import net.petafuel.styx.core.banklookup.sad.entities.ImplementerOption;
import net.petafuel.styx.core.persistence.layers.PersistentPayment;
import net.petafuel.styx.core.xs2a.XS2APaymentInitiationRequest;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.BulkPayment;
import net.petafuel.styx.core.xs2a.entities.Payment;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.BulkPaymentInitiationJsonRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PaymentInitiationJsonRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PaymentInitiationPain001Request;
import net.petafuel.styx.core.xs2a.utils.PaymentXMLSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.UUID;

@Path("/v1")
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@CheckAccessToken
@RequiresPSU
public class PaymentResource extends PSUResource {

    private static final Logger LOG = LogManager.getLogger(PaymentResource.class);

    @HeaderParam("token")
    private String token;

    /**
     * Initiates single and future payments on the aspsp interface
     *
     * @param bic
     * @param paymentProductBean
     * @param paymentInitiationRequest
     * @return
     * @throws BankLookupFailedException
     * @throws BankNotFoundException
     * @throws BankRequestFailedException
     */
    @POST
    @Path("/payments/{paymentProduct}")
    @RequiresBIC
    public Response initiateSinglePayment(@HeaderParam(XS2AHeader.PSU_BIC) String bic, @BeanParam PaymentProductBean paymentProductBean, @Valid PaymentInitiation paymentInitiationRequest) throws BankLookupFailedException, BankNotFoundException, BankRequestFailedException {
        Optional<Payment> singlePayment = paymentInitiationRequest.getPayments().stream().findFirst();
        if (!singlePayment.isPresent()) {
            throw new StyxException(new ErrorEntity("No valid payment object was found within the payments array", Response.Status.BAD_REQUEST, ErrorCategory.STYX));
        }

        XS2AStandard xs2AStandard = (new SAD()).getBankByBIC(bic, WebServer.isSandbox());
        Payment payment = singlePayment.get();

        //Check if payment is a future payment and if aspsp supports this
        if (payment.getRequestedExecutionDate() != null) {
            ImplementerOption supportFutureDatedPayments = xs2AStandard.getAspsp().getConfig().getImplementerOptions().get("IO21");
            if (!supportFutureDatedPayments.getOptions().get("available").getAsBoolean()) {
                throw new StyxException(new ErrorEntity("ASPSP does not support future-dated payments but requestedExecutionDate was set", Response.Status.BAD_REQUEST, ErrorCategory.STYX));
            }
        }

        XS2APaymentInitiationRequest singlePaymentInitiation;
        //check IO2 for xml or json
        ImplementerOption supportedSinglePaymentProduct = xs2AStandard.getAspsp().getConfig().getImplementerOptions().get("IO2");
        if (supportedSinglePaymentProduct.getOptions().get(paymentProductBean.getPaymentProduct().toString()).getAsBoolean()) {
            //aspsp accepts json
            singlePaymentInitiation = new PaymentInitiationJsonRequest(paymentProductBean.getPaymentProduct(), payment, getPsu());
        } else {
            //aspsp does not support json, use pain001.003
            PAIN00100303Document document = (new PaymentXMLSerializer()).serialize("test", payment);

            singlePaymentInitiation = new PaymentInitiationPain001Request(paymentProductBean.getPaymentProduct(), PaymentService.PAYMENTS, document, getPsu());
        }
        PaymentResponse paymentResponse = new PaymentResponse(xs2AStandard.getPis().initiatePayment(singlePaymentInitiation));
        LOG.info("Initiate single payment bic={} aspsp_name={} aspsp_id={} paymentId={}", bic, xs2AStandard.getAspsp().getName(), xs2AStandard.getAspsp().getId(), paymentResponse.getPaymentId());
        PersistentPayment.create(paymentResponse.getPaymentId(), UUID.fromString(token), bic, paymentResponse.getTransactionStatus());
        return Response.status(200).entity(paymentResponse).build();
    }

    @POST
    @Path("/bulk-payments/{paymentProduct}")
    @RequiresBIC
    public Response initiateBulkPayment(@HeaderParam(XS2AHeader.PSU_BIC) String bic,
                                        @BeanParam PaymentProductBean paymentProductBean,
                                        @Valid BulkPaymentInitiation paymentInitiationRequest) throws BankLookupFailedException, BankNotFoundException, BankRequestFailedException {
        XS2AStandard xs2AStandard = (new SAD()).getBankByBIC(bic, WebServer.isSandbox());
        Optional<Payment> singlePayment = paymentInitiationRequest.getPayments().stream().findAny();
        if (!singlePayment.isPresent()) {
            throw new StyxException(new ErrorEntity("No valid payment object was found within the payments array", Response.Status.BAD_REQUEST, ErrorCategory.STYX));
        }
        XS2APaymentInitiationRequest bulkPaymentInitiation;

        Account debtor = singlePayment.get().getDebtor();
        BulkPayment bulkPayment = new BulkPayment();
        bulkPayment.setBatchBookingPreferred(paymentInitiationRequest.getBatchBookingPreferred());
        bulkPayment.setDebtorAccount(debtor);
        bulkPayment.setPayments(paymentInitiationRequest.getPayments());
        bulkPayment.setRequestedExecutionDate(paymentInitiationRequest.getRequestedExecutionDate());

        ImplementerOption supportedSinglePaymentProduct = xs2AStandard.getAspsp().getConfig().getImplementerOptions().get("IO3");
        if (supportedSinglePaymentProduct.getOptions().get(paymentProductBean.getPaymentProduct().toString()).getAsBoolean()) {
            bulkPaymentInitiation = new BulkPaymentInitiationJsonRequest(paymentProductBean.getPaymentProduct(), bulkPayment, getPsu());
        } else {
            PAIN00100303Document document = (new PaymentXMLSerializer()).serialize("test", bulkPayment);
        }

        PaymentResponse paymentResponse = new PaymentResponse(xs2AStandard.getPis().initiatePayment(bulkPaymentInitiation));
        LOG.info("Initiate bulk payment bic={} aspsp_name={} aspsp_id={} paymentId={}", bic, xs2AStandard.getAspsp().getName(), xs2AStandard.getAspsp().getId(), paymentResponse.getPaymentId());
        PersistentPayment.create(paymentResponse.getPaymentId(), UUID.fromString(token), bic, paymentResponse.getTransactionStatus());
        return Response.status(200).entity(paymentResponse).build();
    }

    @POST
    @Path("/periodic-payments/{paymentProduct}")
    public Response initiatePeriodicPayment(@HeaderParam("bic") String bic, @BeanParam PaymentProductBean paymentProductBean, @Valid PaymentInitiation paymentInitiationRequest) throws BankLookupFailedException, BankNotFoundException, BankRequestFailedException {
        XS2AStandard xs2AStandard = (new SAD()).getBankByBIC(bic, WebServer.isSandbox());
        LOG.info("Initiate periodic payment bic={} aspsp_name={} aspsp_id={}", bic, xs2AStandard.getAspsp().getName(), xs2AStandard.getAspsp().getId());

        //TODO check IO4 for xml or json
        PaymentInitiationJsonRequest paymentInitiationJsonRequest = new PaymentInitiationJsonRequest(paymentProductBean.getPaymentProduct(), paymentInitiationRequest.getPayments().get(0), getPsu());
        PaymentResponse paymentResponse = new PaymentResponse(xs2AStandard.getPis().initiatePayment(paymentInitiationJsonRequest));
        return Response.status(200).entity(paymentResponse).build();
    }
}
