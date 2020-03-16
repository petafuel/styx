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
import net.petafuel.styx.api.v1.payment.entity.PaymentTypeBean;
import net.petafuel.styx.api.v1.payment.entity.StartSCARequest;
import net.petafuel.styx.core.xs2a.contracts.XS2AAuthorisationRequest;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.GetAuthorisationsRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.GetSCAStatusRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.StartAuthorisationRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.*;
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

    @POST
    @Path("/{paymentService}/{paymentProduct}/{paymentId}/authorisations")
    public Response startPaymentAuthorisation(@BeanParam PaymentTypeBean paymentTypeBean,
                                              @NotEmpty @NotBlank @PathParam("paymentId") String paymentId,
                                              @Valid StartSCARequest startSCARequest) throws BankRequestFailedException {
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
                startSCARequest.getPsuData(),
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
