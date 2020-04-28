package net.petafuel.styx.api.v1.payment.boundary;

import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.filter.AbstractTokenFilter;
import net.petafuel.styx.api.filter.CheckAccessToken;
import net.petafuel.styx.api.filter.RequiresBIC;
import net.petafuel.styx.api.filter.RequiresMandatoryHeader;
import net.petafuel.styx.api.filter.RequiresPSU;
import net.petafuel.styx.api.rest.PSUResource;
import net.petafuel.styx.api.service.SADService;
import net.petafuel.styx.api.util.AspspUrlMapper;
import net.petafuel.styx.api.v1.payment.control.PaymentInitiationProvider;
import net.petafuel.styx.api.v1.payment.entity.BulkPaymentInitiation;
import net.petafuel.styx.api.v1.payment.entity.PaymentResponse;
import net.petafuel.styx.api.v1.payment.entity.PaymentTypeBean;
import net.petafuel.styx.api.v1.payment.entity.PeriodicPaymentInitiation;
import net.petafuel.styx.api.v1.payment.entity.SinglePaymentInitiation;
import net.petafuel.styx.core.persistence.layers.PersistentPayment;
import net.petafuel.styx.core.persistence.models.AccessToken;
import net.petafuel.styx.core.xs2a.XS2APaymentRequest;
import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.sca.OAuth2;
import net.petafuel.styx.core.xs2a.sca.SCAApproach;
import net.petafuel.styx.core.xs2a.sca.SCAHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @documented https://confluence.petafuel.intern/display/TOOL/Styx+PIS+-+Interface+Definition#StyxPISInterfaceDefinition-PaymentInitiationRequest
 */
@Path("/v1")
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@Consumes({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@CheckAccessToken(allowedServices = {AccessToken.ServiceType.AISPIS, AccessToken.ServiceType.PIS})
@RequiresPSU
@RequiresBIC
public class PaymentInitiationResource extends PSUResource {
    private static final Logger LOG = LogManager.getLogger(PaymentInitiationResource.class);

    @Inject
    private SADService sadService;

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
    public Response initiateSinglePayment(@BeanParam PaymentTypeBean paymentTypeBean,
                                          @Valid SinglePaymentInitiation singlePaymentBody) throws BankRequestFailedException {
        XS2APaymentRequest aspspRequest = new PaymentInitiationProvider(sadService.getXs2AStandard(), paymentTypeBean, getPsu()).buildSinglePaymentRequest(singlePaymentBody);
        aspspRequest.setTppRedirectPreferred(getRedirectPreferred());
        aspspRequest.getHeaders().putAll(getSandboxHeaders());
        InitiatedPayment initiatedPayment = sadService.getXs2AStandard().getPis().initiatePayment(aspspRequest);
        PaymentResponse paymentResponse = new PaymentResponse(initiatedPayment);
        SCAApproach approach = SCAHandler.decision(initiatedPayment);
        if (approach instanceof OAuth2) {
            paymentResponse.getLinks().getScaOAuth().setUrl(((OAuth2) approach).getAuthoriseLink());
        }

        LOG.info("Initiate single payment bic={} aspsp_name={} aspsp_id={} paymentId={}", sadService.getXs2AStandard().getAspsp().getBic(), sadService.getXs2AStandard().getAspsp().getName(), sadService.getXs2AStandard().getAspsp().getId(), paymentResponse.getPaymentId());

        AspspUrlMapper aspspUrlMapper = new AspspUrlMapper(PaymentService.PAYMENTS, paymentTypeBean.getPaymentProduct(), paymentResponse.getPaymentId(), null);
        paymentResponse.setLinks(aspspUrlMapper.map(paymentResponse.getLinks()));

        PersistentPayment.create(paymentResponse.getPaymentId(), (String) getContainerRequestContext().getProperty(AbstractTokenFilter.class.getName()), sadService.getXs2AStandard().getAspsp().getBic(), paymentResponse.getTransactionStatus());
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
    public Response initiateBulkPayment(
            @BeanParam PaymentTypeBean paymentTypeBean,
            @Valid BulkPaymentInitiation bulkPaymentBody) throws BankRequestFailedException {
        XS2APaymentRequest aspspRequest = new PaymentInitiationProvider(sadService.getXs2AStandard(), paymentTypeBean, getPsu()).buildBulkPaymentRequest(bulkPaymentBody);
        aspspRequest.setTppRedirectPreferred(getRedirectPreferred());
        aspspRequest.getHeaders().putAll(getSandboxHeaders());
        InitiatedPayment initiatedPayment = sadService.getXs2AStandard().getPis().initiatePayment(aspspRequest);
        PaymentResponse paymentResponse = new PaymentResponse(initiatedPayment);
        SCAApproach approach = SCAHandler.decision(initiatedPayment);
        if (approach instanceof OAuth2) {
            paymentResponse.getLinks().getScaOAuth().setUrl(((OAuth2) approach).getAuthoriseLink());
        }
        LOG.info("Initiate bulk payment bic={} aspsp_name={} aspsp_id={} paymentId={}", sadService.getXs2AStandard().getAspsp().getBic(), sadService.getXs2AStandard().getAspsp().getName(), sadService.getXs2AStandard().getAspsp().getId(), paymentResponse.getPaymentId());

        AspspUrlMapper aspspUrlMapper = new AspspUrlMapper(PaymentService.BULK_PAYMENTS, paymentTypeBean.getPaymentProduct(), paymentResponse.getPaymentId(), null);
        paymentResponse.setLinks(aspspUrlMapper.map(paymentResponse.getLinks()));

        PersistentPayment.create(paymentResponse.getPaymentId(), (String) getContainerRequestContext().getProperty(AbstractTokenFilter.class.getName()), sadService.getXs2AStandard().getAspsp().getBic(), paymentResponse.getTransactionStatus());
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
    public Response initiatePeriodicPayment(@BeanParam PaymentTypeBean paymentTypeBean,
                                            @Valid PeriodicPaymentInitiation periodicPaymentBody) throws BankRequestFailedException {
        XS2APaymentRequest aspspRequest = new PaymentInitiationProvider(sadService.getXs2AStandard(), paymentTypeBean, getPsu()).buildPeriodicPaymentRequest(periodicPaymentBody);
        aspspRequest.setTppRedirectPreferred(getRedirectPreferred());
        aspspRequest.getHeaders().putAll(getSandboxHeaders());
        InitiatedPayment initiatedPayment = sadService.getXs2AStandard().getPis().initiatePayment(aspspRequest);
        PaymentResponse paymentResponse = new PaymentResponse(initiatedPayment);
        SCAApproach approach = SCAHandler.decision(initiatedPayment);
        if (approach instanceof OAuth2) {
            paymentResponse.getLinks().getScaOAuth().setUrl(((OAuth2) approach).getAuthoriseLink());
        }
        LOG.info("Initiate periodic payment bic={} aspsp_name={} aspsp_id={}", sadService.getXs2AStandard().getAspsp().getBic(), sadService.getXs2AStandard().getAspsp().getName(), sadService.getXs2AStandard().getAspsp().getId());

        AspspUrlMapper aspspUrlMapper = new AspspUrlMapper(PaymentService.PERIODIC_PAYMENTS, paymentTypeBean.getPaymentProduct(), paymentResponse.getPaymentId(), null);
        paymentResponse.setLinks(aspspUrlMapper.map(paymentResponse.getLinks()));

        PersistentPayment.create(paymentResponse.getPaymentId(), (String) getContainerRequestContext().getProperty(AbstractTokenFilter.class.getName()), sadService.getXs2AStandard().getAspsp().getBic(), paymentResponse.getTransactionStatus());
        return Response.status(ResponseConstant.CREATED).entity(paymentResponse).build();
    }
}
