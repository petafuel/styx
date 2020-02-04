package net.petafuel.styx.api.v1.payment.boundary;

import net.petafuel.styx.api.WebServer;
import net.petafuel.styx.api.filter.CheckAccessToken;
import net.petafuel.styx.api.filter.RequiresBIC;
import net.petafuel.styx.api.filter.RequiresMandatoryHeader;
import net.petafuel.styx.api.filter.RequiresPSU;
import net.petafuel.styx.api.rest.PSUResource;
import net.petafuel.styx.api.v1.payment.control.PaymentInitiationProvider;
import net.petafuel.styx.api.v1.payment.entity.BulkPaymentInitiation;
import net.petafuel.styx.api.v1.payment.entity.PaymentProductBean;
import net.petafuel.styx.api.v1.payment.entity.PaymentResponse;
import net.petafuel.styx.api.v1.payment.entity.PeriodicPaymentInitiation;
import net.petafuel.styx.api.v1.payment.entity.SinglePaymentInitiation;
import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.banklookup.exceptions.BankLookupFailedException;
import net.petafuel.styx.core.banklookup.exceptions.BankNotFoundException;
import net.petafuel.styx.core.banklookup.sad.SAD;
import net.petafuel.styx.core.persistence.layers.PersistentPayment;
import net.petafuel.styx.core.xs2a.XS2APaymentInitiationRequest;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
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
import java.util.UUID;

@Path("/v1")
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@CheckAccessToken
@RequiresPSU
public class PaymentInitiationResource extends PSUResource {
    private static final Logger LOG = LogManager.getLogger(PaymentInitiationResource.class);

    @HeaderParam("token")
    private String token;

    /**
     * Initiates single and future payments on the aspsp interface
     *
     * @param bic
     * @param paymentProductBean
     * @param singlePaymentBody
     * @return
     * @throws BankLookupFailedException
     * @throws BankNotFoundException
     * @throws BankRequestFailedException
     */
    @POST
    @Path("/payments/{paymentProduct}")
    @RequiresBIC
    @RequiresMandatoryHeader
    public Response initiateSinglePayment(@HeaderParam(XS2AHeader.PSU_BIC) String bic,
                                          @BeanParam PaymentProductBean paymentProductBean,
                                          @Valid SinglePaymentInitiation singlePaymentBody) throws BankLookupFailedException, BankNotFoundException, BankRequestFailedException {
        XS2AStandard xs2AStandard = (new SAD()).getBankByBIC(bic, WebServer.isSandbox());

        XS2APaymentInitiationRequest aspspRequest = new PaymentInitiationProvider(xs2AStandard, paymentProductBean, getPsu()).buildSinglePaymentRequest(singlePaymentBody);
        aspspRequest.setTppRedirectPreferred(getRedirectPreferred());

        PaymentResponse paymentResponse = new PaymentResponse(xs2AStandard.getPis().initiatePayment(aspspRequest));
        LOG.info("Initiate single payment bic={} aspsp_name={} aspsp_id={} paymentId={}", bic, xs2AStandard.getAspsp().getName(), xs2AStandard.getAspsp().getId(), paymentResponse.getPaymentId());

        PersistentPayment.create(paymentResponse.getPaymentId(), UUID.fromString(token), bic, paymentResponse.getTransactionStatus());
        return Response.status(201).entity(paymentResponse).build();
    }

    /**
     * Initiate bulk payments
     *
     * @param bic
     * @param paymentProductBean
     * @param bulkPaymentBody
     * @return
     * @throws BankLookupFailedException
     * @throws BankNotFoundException
     * @throws BankRequestFailedException
     */
    @POST
    @Path("/bulk-payments/{paymentProduct}")
    @RequiresBIC
    @RequiresMandatoryHeader
    public Response initiateBulkPayment(@HeaderParam(XS2AHeader.PSU_BIC) String bic,
                                        @BeanParam PaymentProductBean paymentProductBean,
                                        @Valid BulkPaymentInitiation bulkPaymentBody) throws BankLookupFailedException, BankNotFoundException, BankRequestFailedException {
        XS2AStandard xs2AStandard = (new SAD()).getBankByBIC(bic, WebServer.isSandbox());
        XS2APaymentInitiationRequest aspspRequest = new PaymentInitiationProvider(xs2AStandard, paymentProductBean, getPsu()).buildBulkPaymentRequest(bulkPaymentBody);

        aspspRequest.setTppRedirectPreferred(getRedirectPreferred());

        PaymentResponse paymentResponse = new PaymentResponse(xs2AStandard.getPis().initiatePayment(aspspRequest));
        LOG.info("Initiate bulk payment bic={} aspsp_name={} aspsp_id={} paymentId={}", bic, xs2AStandard.getAspsp().getName(), xs2AStandard.getAspsp().getId(), paymentResponse.getPaymentId());
        PersistentPayment.create(paymentResponse.getPaymentId(), UUID.fromString(token), bic, paymentResponse.getTransactionStatus());
        return Response.status(201).entity(paymentResponse).build();
    }

    /**
     * @param bic
     * @param paymentProductBean
     * @param periodicPaymentBody
     * @return
     * @throws BankLookupFailedException
     * @throws BankNotFoundException
     * @throws BankRequestFailedException
     */
    @POST
    @Path("/periodic-payments/{paymentProduct}")
    @RequiresMandatoryHeader
    @RequiresBIC
    public Response initiatePeriodicPayment(@HeaderParam(XS2AHeader.PSU_BIC) String bic,
                                            @BeanParam PaymentProductBean paymentProductBean,
                                            @Valid PeriodicPaymentInitiation periodicPaymentBody) throws BankLookupFailedException, BankNotFoundException, BankRequestFailedException {
        XS2AStandard xs2AStandard = (new SAD()).getBankByBIC(bic, WebServer.isSandbox());

        XS2APaymentInitiationRequest aspspRequest = new PaymentInitiationProvider(xs2AStandard, paymentProductBean, getPsu()).buildPeriodicPaymentRequest(periodicPaymentBody);
        aspspRequest.setTppRedirectPreferred(getRedirectPreferred());

        PaymentResponse paymentResponse = new PaymentResponse(xs2AStandard.getPis().initiatePayment(aspspRequest));
        LOG.info("Initiate periodic payment bic={} aspsp_name={} aspsp_id={}", bic, xs2AStandard.getAspsp().getName(), xs2AStandard.getAspsp().getId());
        return Response.status(201).entity(paymentResponse).build();
    }
}
