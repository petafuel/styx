package net.petafuel.styx.api.v1.payment.boundary;

import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.filter.AcceptsPreStepAuth;
import net.petafuel.styx.api.filter.CheckAccessToken;
import net.petafuel.styx.api.filter.RequiresBIC;
import net.petafuel.styx.api.filter.RequiresPSU;
import net.petafuel.styx.api.rest.RestResource;
import net.petafuel.styx.api.util.AspspUrlMapper;
import net.petafuel.styx.api.util.io.IOProcessor;
import net.petafuel.styx.api.v1.payment.entity.AuthorisationIdsResponse;
import net.petafuel.styx.api.v1.payment.entity.AuthorisationRequest;
import net.petafuel.styx.api.v1.payment.entity.AuthorisationStatusResponse;
import net.petafuel.styx.api.v1.payment.entity.PaymentTypeBean;
import net.petafuel.styx.core.persistence.models.AccessToken;
import net.petafuel.styx.core.xs2a.contracts.SCARequest;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.factory.SCARequestFactory;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/v1")
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@Consumes({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@CheckAccessToken(allowedServices = {AccessToken.ServiceType.AISPIS, AccessToken.ServiceType.PIS})
@RequiresPSU
@RequiresBIC
public class PaymentAuthorisationResource extends RestResource {
    private static final Logger LOG = LogManager.getLogger(PaymentAuthorisationResource.class);

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
    @AcceptsPreStepAuth
    @POST
    @Path("/{paymentService}/{paymentProduct}/{paymentId}/authorisations")
    public Response startPaymentAuthorisation(@BeanParam PaymentTypeBean paymentTypeBean,
                                              @NotEmpty @NotBlank @PathParam("paymentId") String paymentId,
                                              @Valid AuthorisationRequest authorisationRequest) throws BankRequestFailedException {
        XS2AFactoryInput xs2AFactoryInput = new XS2AFactoryInput();
        xs2AFactoryInput.setPaymentService(paymentTypeBean.getPaymentService());
        xs2AFactoryInput.setPaymentProduct(paymentTypeBean.getPaymentProduct());
        xs2AFactoryInput.setPsu(getPsu());
        xs2AFactoryInput.setPaymentId(paymentId);
        xs2AFactoryInput.setPsuData(authorisationRequest.getPsuData());

        IOProcessor ioProcessor = new IOProcessor(getXS2AStandard());
        ioProcessor.modifyInput(xs2AFactoryInput);

        SCARequest xs2AAuthorisationRequest = new SCARequestFactory().create(getXS2AStandard().getRequestClassProvider().scaStart(), xs2AFactoryInput);
        if (getRedirectPreferred() != null) {
            xs2AAuthorisationRequest.setTppRedirectPreferred(getRedirectPreferred());
        }
        xs2AAuthorisationRequest.getHeaders().putAll(getAdditionalHeaders());

        ioProcessor.modifyRequest(xs2AAuthorisationRequest, xs2AFactoryInput);

        SCA paymentSCA = getXS2AStandard().getPis().startAuthorisation(xs2AAuthorisationRequest);

        AspspUrlMapper aspspUrlMapper = new AspspUrlMapper(paymentTypeBean.getPaymentService(),
                paymentTypeBean.getPaymentProduct(),
                paymentId,
                paymentSCA.getAuthorisationId());
        paymentSCA.setLinks(aspspUrlMapper.map(paymentSCA.getLinks()));

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
    @AcceptsPreStepAuth
    @PUT
    @Path("/{paymentService}/{paymentProduct}/{paymentId}/authorisations/{authorisationId}")
    public Response updatePaymentAuthorisation(@BeanParam PaymentTypeBean paymentTypeBean,
                                               @NotEmpty @NotBlank @PathParam("paymentId") String paymentId,
                                               @NotEmpty @NotBlank @PathParam("authorisationId") String authorisationId,
                                               @Valid AuthorisationRequest authorisationRequest) throws BankRequestFailedException {
        XS2AFactoryInput xs2AFactoryInput = new XS2AFactoryInput();
        xs2AFactoryInput.setPaymentService(paymentTypeBean.getPaymentService());
        xs2AFactoryInput.setPaymentProduct(paymentTypeBean.getPaymentProduct());
        xs2AFactoryInput.setPaymentId(paymentId);
        xs2AFactoryInput.setPsu(getPsu());

        xs2AFactoryInput.setAuthorisationId(authorisationId);
        xs2AFactoryInput.setPsuData(authorisationRequest.getPsuData());
        xs2AFactoryInput.setAuthorisationMethodId(authorisationRequest.getAuthenticationMethodId());
        xs2AFactoryInput.setScaAuthenticationData(authorisationRequest.getScaAuthenticationData());

        IOProcessor ioProcessor = new IOProcessor(getXS2AStandard());
        ioProcessor.modifyInput(xs2AFactoryInput);

        SCA paymentSCA;
        SCARequest xs2AAuthorisationRequest;

        if (authorisationRequest.getPsuData() != null) {
            xs2AAuthorisationRequest = new SCARequestFactory().create(getXS2AStandard().getRequestClassProvider().scaUpdateAuthentication(), xs2AFactoryInput);
            xs2AAuthorisationRequest.getHeaders().putAll(getAdditionalHeaders());
            ioProcessor.modifyRequest(xs2AAuthorisationRequest, xs2AFactoryInput);
            paymentSCA = getXS2AStandard().getPis().updatePSUAuthentication(xs2AAuthorisationRequest);
        } else if (authorisationRequest.getAuthenticationMethodId() != null) {
            xs2AAuthorisationRequest = new SCARequestFactory().create(getXS2AStandard().getRequestClassProvider().scaUpdateAuthenticationMethod(), xs2AFactoryInput);
            xs2AAuthorisationRequest.getHeaders().putAll(getAdditionalHeaders());
            ioProcessor.modifyRequest(xs2AAuthorisationRequest, xs2AFactoryInput);
            paymentSCA = getXS2AStandard().getPis().selectAuthenticationMethod(xs2AAuthorisationRequest);
        } else if (authorisationRequest.getScaAuthenticationData() != null) {
            xs2AAuthorisationRequest = new SCARequestFactory().create(getXS2AStandard().getRequestClassProvider().scaAuthoriseTransaction(), xs2AFactoryInput);
            xs2AAuthorisationRequest.getHeaders().putAll(getAdditionalHeaders());
            ioProcessor.modifyRequest(xs2AAuthorisationRequest, xs2AFactoryInput);
            paymentSCA = getXS2AStandard().getPis().authoriseTransaction(xs2AAuthorisationRequest);
        } else {
            xs2AAuthorisationRequest = new SCARequestFactory().create(getXS2AStandard().getRequestClassProvider().scaUpdateIdentification(), xs2AFactoryInput);
            xs2AAuthorisationRequest.getHeaders().putAll(getAdditionalHeaders());
            ioProcessor.modifyRequest(xs2AAuthorisationRequest, xs2AFactoryInput);
            paymentSCA = getXS2AStandard().getPis().updatePSUIdentification(xs2AAuthorisationRequest);
        }

        AspspUrlMapper aspspUrlMapper = new AspspUrlMapper(paymentTypeBean.getPaymentService(),
                paymentTypeBean.getPaymentProduct(),
                paymentId,
                authorisationId);
        paymentSCA.setLinks(aspspUrlMapper.map(paymentSCA.getLinks()));

        LOG.info("Payment Authorisation updated for paymentId={} authorisationId={} scaStatus={} scaApproach={}", paymentId, authorisationId, paymentSCA.getScaStatus(), paymentSCA.getApproach());
        return Response.status(ResponseConstant.OK).entity(paymentSCA).build();
    }

    /**
     * https://confluence.petafuel.intern/display/TOOL/Styx+PIS+-+Interface+Definition#StyxPISInterfaceDefinition-FetchmultipleSCAsforapayment
     *
     * @param paymentTypeBean
     * @param paymentId
     * @return
     * @throws BankRequestFailedException
     */
    @AcceptsPreStepAuth
    @GET
    @Path("/{paymentService}/{paymentProduct}/{paymentId}/authorisations")
    public Response getAuthorisationIds(@BeanParam PaymentTypeBean paymentTypeBean,
                                        @NotEmpty @NotBlank @PathParam("paymentId") String paymentId) throws BankRequestFailedException {
        XS2AFactoryInput xs2AFactoryInput = new XS2AFactoryInput();
        xs2AFactoryInput.setPaymentService(paymentTypeBean.getPaymentService());
        xs2AFactoryInput.setPaymentProduct(paymentTypeBean.getPaymentProduct());
        xs2AFactoryInput.setPaymentId(paymentId);
        xs2AFactoryInput.setPsu(getPsu());

        IOProcessor ioProcessor = new IOProcessor(getXS2AStandard());
        ioProcessor.modifyInput(xs2AFactoryInput);

        SCARequest getAuthorisationRequest = new SCARequestFactory().create(getXS2AStandard().getRequestClassProvider().scaRetrieval(), xs2AFactoryInput);
        getAuthorisationRequest.getHeaders().putAll(getAdditionalHeaders());

        ioProcessor.modifyRequest(getAuthorisationRequest, xs2AFactoryInput);

        List<String> authorisationIds = getXS2AStandard().getPis().getAuthorisations(getAuthorisationRequest);
        AuthorisationIdsResponse response = new AuthorisationIdsResponse();
        response.setAuthorisationIds(authorisationIds);
        LOG.info("Successfully fetched Authorisation ids for payment={}", paymentId);
        return Response.status(ResponseConstant.OK).entity(response).build();
    }

    /**
     * https://confluence.petafuel.intern/display/TOOL/Styx+PIS+-+Interface+Definition#StyxPISInterfaceDefinition-GetAuthorisationSCAStatus
     *
     * @param paymentTypeBean
     * @param paymentId
     * @param authorisationId
     * @return
     * @throws BankRequestFailedException
     */
    @AcceptsPreStepAuth
    @GET
    @Path("/{paymentService}/{paymentProduct}/{paymentId}/authorisations/{authorisationId}")
    public Response getScaStatus(@BeanParam PaymentTypeBean paymentTypeBean,
                                 @NotEmpty @NotBlank @PathParam("paymentId") String paymentId,
                                 @NotEmpty @NotBlank @PathParam("authorisationId") String authorisationId) throws BankRequestFailedException {
        XS2AFactoryInput xs2AFactoryInput = new XS2AFactoryInput();
        xs2AFactoryInput.setPaymentService(paymentTypeBean.getPaymentService());
        xs2AFactoryInput.setPaymentProduct(paymentTypeBean.getPaymentProduct());
        xs2AFactoryInput.setPaymentId(paymentId);
        xs2AFactoryInput.setAuthorisationId(authorisationId);
        xs2AFactoryInput.setPsu(getPsu());

        IOProcessor ioProcessor = new IOProcessor(getXS2AStandard());
        ioProcessor.modifyInput(xs2AFactoryInput);

        SCARequest getAuthorisationStatusRequest = new SCARequestFactory().create(getXS2AStandard().getRequestClassProvider().scaStatus(), xs2AFactoryInput);
        getAuthorisationStatusRequest.getHeaders().putAll(getAdditionalHeaders());

        ioProcessor.modifyRequest(getAuthorisationStatusRequest, xs2AFactoryInput);

        SCA.Status authorisationStatus = getXS2AStandard().getPis().getSCAStatus(getAuthorisationStatusRequest);
        AuthorisationStatusResponse response = new AuthorisationStatusResponse();
        response.setScaStatus(authorisationStatus.getValue());
        LOG.info("Successfully fetched Authorisation status for payment={}, authorisationId={}", paymentId, authorisationId);
        return Response.status(ResponseConstant.OK).entity(response).build();
    }
}
