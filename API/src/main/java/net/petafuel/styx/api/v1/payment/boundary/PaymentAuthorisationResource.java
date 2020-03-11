package net.petafuel.styx.api.v1.payment.boundary;

import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.filter.CheckAccessToken;
import net.petafuel.styx.api.filter.RequiresBIC;
import net.petafuel.styx.api.filter.RequiresPSU;
import net.petafuel.styx.api.rest.PSUResource;
import net.petafuel.styx.api.service.SADService;
import net.petafuel.styx.api.util.AspspUrlMapper;
import net.petafuel.styx.api.util.io.IOProcessor;
import net.petafuel.styx.api.util.io.contracts.IOInputContainerPIS;
import net.petafuel.styx.api.v1.payment.entity.AuthorisationRequest;
import net.petafuel.styx.api.v1.payment.entity.PaymentTypeBean;
import net.petafuel.styx.core.xs2a.contracts.XS2AAuthorisationRequest;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.AuthoriseTransactionRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.SelectAuthenticationMethodRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.StartAuthorisationRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.UpdatePSUAuthenticationRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.UpdatePSUIdentificationRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v1")
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@Consumes({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@CheckAccessToken
@RequiresPSU
@RequiresBIC
public class PaymentAuthorisationResource extends PSUResource {
    private static final Logger LOG = LogManager.getLogger(PaymentAuthorisationResource.class);
    @Inject
    private SADService sadService;

    @POST
    @Path("/{paymentService}/{paymentProduct}/{paymentId}/authorisations")
    public Response startPaymentAuthorisation(@BeanParam PaymentTypeBean paymentTypeBean,
                                              @NotEmpty @NotBlank @PathParam("paymentId") String paymentId,
                                              @Valid AuthorisationRequest authorisationRequest) throws BankRequestFailedException {
        IOInputContainerPIS ioInputContainerPIS = new IOInputContainerPIS(
                IOInputContainerPIS.RequestType.FETCH,
                sadService.getXs2AStandard(),
                getPsu(),
                paymentId,
                paymentTypeBean.getPaymentService(),
                paymentTypeBean.getPaymentProduct());

        IOProcessor ioProcessor = new IOProcessor(ioInputContainerPIS);
        ioProcessor.applyOptions();
        XS2AAuthorisationRequest xs2AAuthorisationRequest = new StartAuthorisationRequest(getPsu(),
                authorisationRequest.getPsuData(),
                paymentTypeBean.getPaymentService(),
                ioProcessor.getIoInputContainerpis().getPaymentProduct(), paymentId);
        xs2AAuthorisationRequest.getHeaders().putAll(getSandboxHeaders());
        SCA paymentSCA = sadService.getXs2AStandard().getPis().startAuthorisation(xs2AAuthorisationRequest);

        AspspUrlMapper aspspUrlMapper = new AspspUrlMapper(paymentTypeBean.getPaymentService(),
                paymentTypeBean.getPaymentProduct(),
                paymentId,
                paymentSCA.getAuthorisationId());
        aspspUrlMapper.map(paymentSCA.getLinks());

        LOG.info("Payment Authorisation started for paymentId={} scaStatus={} scaApproach={}", paymentId, paymentSCA.getScaStatus(), paymentSCA.getApproach());
        return Response.status(ResponseConstant.OK).entity(paymentSCA).build();
    }

    @PUT
    @Path("/{paymentService}/{paymentProduct}/{paymentId}/authorisations/{authorisationId}")
    public Response updatePaymentAuthorisation(@BeanParam PaymentTypeBean paymentTypeBean,
                                               @NotEmpty @NotBlank @PathParam("paymentId") String paymentId,
                                               @NotEmpty @NotBlank @PathParam("authorisationId") String authorisationId,
                                               @Valid AuthorisationRequest authorisationRequest) throws BankRequestFailedException {
        IOInputContainerPIS ioInputContainerPIS = new IOInputContainerPIS(
                IOInputContainerPIS.RequestType.FETCH,
                sadService.getXs2AStandard(),
                getPsu(),
                paymentId,
                paymentTypeBean.getPaymentService(),
                paymentTypeBean.getPaymentProduct());

        IOProcessor ioProcessor = new IOProcessor(ioInputContainerPIS);
        ioProcessor.applyOptions();

        SCA paymentSCA;
        XS2AAuthorisationRequest xs2AAuthorisationRequest;

        if (authorisationRequest.getPsuData() != null) {
            xs2AAuthorisationRequest = new UpdatePSUAuthenticationRequest(
                    getPsu(),
                    authorisationRequest.getPsuData(),
                    ioInputContainerPIS.getPaymentService(),
                    ioInputContainerPIS.getPaymentProduct(),
                    paymentId,
                    authorisationId);
            xs2AAuthorisationRequest.getHeaders().putAll(getSandboxHeaders());
            paymentSCA = sadService.getXs2AStandard().getPis().updatePSUAuthentication(xs2AAuthorisationRequest);
        } else if (authorisationRequest.getAuthenticationMethodId() != null) {
            xs2AAuthorisationRequest = new SelectAuthenticationMethodRequest(
                    authorisationRequest.getAuthenticationMethodId(),
                    ioInputContainerPIS.getPaymentService(),
                    ioInputContainerPIS.getPaymentProduct(),
                    paymentId,
                    authorisationId);
            xs2AAuthorisationRequest.getHeaders().putAll(getSandboxHeaders());
            paymentSCA = sadService.getXs2AStandard().getPis().selectAuthenticationMethod(xs2AAuthorisationRequest);
        } else if (authorisationRequest.getScaAuthenticationData() != null) {
            xs2AAuthorisationRequest = new AuthoriseTransactionRequest(
                    authorisationRequest.getScaAuthenticationData(),
                    ioInputContainerPIS.getPaymentService(),
                    ioInputContainerPIS.getPaymentProduct(),
                    paymentId,
                    authorisationId);
            xs2AAuthorisationRequest.getHeaders().putAll(getSandboxHeaders());
            paymentSCA = sadService.getXs2AStandard().getPis().authoriseTransaction(xs2AAuthorisationRequest);
        } else {
            xs2AAuthorisationRequest = new UpdatePSUIdentificationRequest(
                    getPsu(),
                    ioInputContainerPIS.getPaymentService(),
                    ioInputContainerPIS.getPaymentProduct(),
                    paymentId,
                    authorisationId);
            xs2AAuthorisationRequest.getHeaders().putAll(getSandboxHeaders());
            paymentSCA = sadService.getXs2AStandard().getPis().updatePSUIdentification(xs2AAuthorisationRequest);
        }

        AspspUrlMapper aspspUrlMapper = new AspspUrlMapper(paymentTypeBean.getPaymentService(),
                paymentTypeBean.getPaymentProduct(),
                paymentId,
                authorisationId);
        aspspUrlMapper.map(paymentSCA.getLinks());

        LOG.info("Payment Authorisation updated for paymentId={} authorisationId={} scaStatus={} scaApproach={}", paymentId, authorisationId,paymentSCA.getScaStatus(), paymentSCA.getApproach());
        return Response.status(ResponseConstant.OK).entity(paymentSCA).build();
    }
}
