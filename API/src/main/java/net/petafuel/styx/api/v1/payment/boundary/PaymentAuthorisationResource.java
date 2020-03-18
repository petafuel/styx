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
import net.petafuel.styx.api.v1.payment.entity.AuthorisationIdsResponse;
import net.petafuel.styx.api.v1.payment.entity.AuthorisationStatusResponse;
import net.petafuel.styx.api.v1.payment.entity.AuthorisationRequest;
import net.petafuel.styx.api.v1.payment.entity.PaymentTypeBean;
import net.petafuel.styx.core.xs2a.contracts.XS2AAuthorisationRequest;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.GetAuthorisationsRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.GetSCAStatusRequest;
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
import javax.ws.rs.GET;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

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

    /**
     * If the ASPSP has not implicitly created an authorisation resource, this Endpoint can create an authorisation resource
     *
     * @param paymentTypeBean      payment-service and payment-product
     * @param paymentId            id of the payment an authorisation should be started for
     * @param authorisationRequest the request resource might contain an empty body or PSUData authentication
     * @return returns an SCA container with further information on the Authorisation
     * @throws BankRequestFailedException in case the communication between styx and aspsp was not successful
     * @documented https://confluence.petafuel.intern/display/TOOL/Styx+PIS+-+Interface+Definition#StyxPISInterfaceDefinition-RedPOST/v1/{payment-service}/{payment-product}/{paymentId}/authorisations
     */
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

    /**
     * This endpoint covers 4 use cases
     * Empty authorisationRequest -> PSU Identification, the PSU-* Headers are transmitted to the aspsp
     * PSUData -> PSU Authentication, login the PSU with pin/password on the ASPSP interface
     * authenticationMethodId -> SCAMethod Selection, if there are multiple SCAMethods for the PSU to choose from
     * scaAuthenticationData -> if the PSU has received a TAN for the SCA process we can forward it to the ASPSP
     *
     * @param paymentTypeBean
     * @param paymentId
     * @param authorisationId
     * @param authorisationRequest
     * @return
     * @throws BankRequestFailedException
     * @documented https://confluence.petafuel.intern/display/TOOL/Styx+PIS+-+Interface+Definition#StyxPISInterfaceDefinition-GreenPUT/v1/{payment-service}/{payment-product}/{paymentId}/authorisations/{authorisationId}
     */
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

    /**
     * https://confluence.petafuel.intern/display/TOOL/Styx+PIS+-+Interface+Definition#StyxPISInterfaceDefinition-FetchmultipleSCAsforapayment
     * @param paymentTypeBean
     * @param paymentId
     * @return
     * @throws BankRequestFailedException
     */
    @GET
    @Path("/{paymentService}/{paymentProduct}/{paymentId}/authorisations")
    public Response getAuthorisationIds(@BeanParam PaymentTypeBean paymentTypeBean,
                                        @NotEmpty @NotBlank @PathParam("paymentId") String paymentId) throws BankRequestFailedException {
        IOInputContainerPIS ioInputContainerPIS = new IOInputContainerPIS(
                IOInputContainerPIS.RequestType.FETCH,
                sadService.getXs2AStandard(),
                getPsu(),
                paymentId,
                paymentTypeBean.getPaymentService(),
                paymentTypeBean.getPaymentProduct());

        IOProcessor ioProcessor = new IOProcessor(ioInputContainerPIS);
        ioProcessor.applyOptions();
        XS2AAuthorisationRequest getAuthorisationRequest = new GetAuthorisationsRequest(ioInputContainerPIS.getPaymentService(), ioInputContainerPIS.getPaymentProduct(), paymentId);
        getAuthorisationRequest.getHeaders().putAll(getSandboxHeaders());
        List<String> authorisationIds = sadService.getXs2AStandard().getPis().getAuthorisations(getAuthorisationRequest);
        AuthorisationIdsResponse response = new AuthorisationIdsResponse();
        response.setAuthorisationIds(authorisationIds);

        return Response.status(ResponseConstant.OK).entity(response).build();
    }

    /**
     * https://confluence.petafuel.intern/display/TOOL/Styx+PIS+-+Interface+Definition#StyxPISInterfaceDefinition-GetAuthorisationSCAStatus
     * @param paymentTypeBean
     * @param paymentId
     * @param authorisationId
     * @return
     * @throws BankRequestFailedException
     */
    @GET
    @Path("/{paymentService}/{paymentProduct}/{paymentId}/authorisations/{authorisationId}")
    public Response getScaStatus(@BeanParam PaymentTypeBean paymentTypeBean,
                                 @NotEmpty @NotBlank @PathParam("paymentId") String paymentId,
                                 @NotEmpty @NotBlank @PathParam("authorisationId") String authorisationId) throws BankRequestFailedException {
        IOInputContainerPIS ioInputContainerPIS = new IOInputContainerPIS(
                IOInputContainerPIS.RequestType.FETCH,
                sadService.getXs2AStandard(),
                getPsu(),
                paymentId,
                paymentTypeBean.getPaymentService(),
                paymentTypeBean.getPaymentProduct());

        IOProcessor ioProcessor = new IOProcessor(ioInputContainerPIS);
        ioProcessor.applyOptions();
        XS2AAuthorisationRequest getAuthorisationStatusRequest = new GetSCAStatusRequest(ioInputContainerPIS.getPaymentService(), ioInputContainerPIS.getPaymentProduct(), paymentId, authorisationId);
        getAuthorisationStatusRequest.getHeaders().putAll(getSandboxHeaders());
        SCA.Status authorisationStatus = sadService.getXs2AStandard().getPis().getSCAStatus(getAuthorisationStatusRequest);
        AuthorisationStatusResponse response = new AuthorisationStatusResponse();
        response.setScaStatus(authorisationStatus.getValue());

        return Response.status(ResponseConstant.OK).entity(response).build();
    }
}
