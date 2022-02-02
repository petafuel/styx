package net.petafuel.styx.api.v1.consent.boundary;

import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.filter.authentication.boundary.AcceptsPreStepAuth;
import net.petafuel.styx.api.filter.authentication.boundary.CheckAccessToken;
import net.petafuel.styx.api.filter.input.boundary.RequiresBIC;
import net.petafuel.styx.api.filter.input.boundary.RequiresMandatoryHeader;
import net.petafuel.styx.api.filter.input.boundary.RequiresPSU;
import net.petafuel.styx.api.rest.RestResource;
import net.petafuel.styx.api.util.AspspUrlMapper;
import net.petafuel.styx.api.util.Sanitizer;
import net.petafuel.styx.api.v1.payment.entity.AuthorisationRequest;
import net.petafuel.styx.api.v1.payment.entity.AuthorisationStatusResponse;
import net.petafuel.styx.core.ioprocessing.IOProcessor;
import net.petafuel.styx.core.xs2a.contracts.SCARequest;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.factory.SCARequestFactory;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;
import net.petafuel.styx.spi.tokentypemapper.api.XS2ATokenType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationPath("/")
@Path("/v1")
@Consumes({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@CheckAccessToken(allowedServices = {XS2ATokenType.AIS, XS2ATokenType.AISPIS})
@RequiresBIC
@RequiresMandatoryHeader
public class ConsentAuthorisationResource extends RestResource {
    private static final Logger LOG = LogManager.getLogger(ConsentAuthorisationResource.class);


    /**
     * Starts a consent authorisation
     *
     * @param consentId of the target consent
     * @return a GetConsentResponse object
     * @throws BankRequestFailedException if something went wrong between the core service and the aspsp
     */
    @AcceptsPreStepAuth
    @POST
    @RequiresPSU
    @Path("/consents/{consentId}/authorisations")
    public Response startConsentAuthorisation(@NotEmpty @NotBlank @PathParam("consentId") String consentId,
                                              @Valid AuthorisationRequest authorisationRequest) throws BankRequestFailedException {
        consentId = Sanitizer.replaceEscSeq(consentId);
        XS2AFactoryInput xs2AFactoryInput = new XS2AFactoryInput();
        xs2AFactoryInput.setConsentId(consentId);
        xs2AFactoryInput.setPsuData(authorisationRequest.getPsuData());

        IOProcessor ioProcessor = new IOProcessor(getXS2AStandard());
        ioProcessor.modifyInput(xs2AFactoryInput);

        SCARequest xs2AAuthorisationRequest = new SCARequestFactory().create(getXS2AStandard().getRequestClassProvider().scaStart(), xs2AFactoryInput);
        xs2AAuthorisationRequest.getHeaders().putAll(getAdditionalHeaders());
        if (getRedirectPreferred() != null) {
            xs2AAuthorisationRequest.setTppRedirectPreferred(getRedirectPreferred());
        }

        ioProcessor.modifyRequest(xs2AAuthorisationRequest, xs2AFactoryInput);
        SCA consentSCA = getXS2AStandard().getCs().startAuthorisation(xs2AAuthorisationRequest);

        AspspUrlMapper aspspUrlMapper = new AspspUrlMapper(consentId, consentSCA.getAuthorisationId());
        consentSCA.setLinks(aspspUrlMapper.map(consentSCA.getLinks()));
        LOG.info("Consent Authorisation started for consentId={} scaStatus={} scaApproach={}", consentId, consentSCA.getScaStatus(), consentSCA.getApproach());
        return Response.status(ResponseConstant.CREATED).entity(consentSCA).build();
    }

    /**
     * This endpoint covers 4 use cases
     * Empty authorisationRequest -> PSU Identification, the PSU-* Headers are transmitted to the aspsp
     * PSUData -> PSU Authentication, login the PSU with pin/password on the ASPSP interface
     * authenticationMethodId -> SCAMethod Selection, if there are multiple SCAMethods for the PSU to choose from
     * scaAuthenticationData -> if the PSU has received a TAN for the SCA process we can forward it to the ASPSP
     *
     * @param consentId
     * @param authorisationId
     * @param authorisationRequest
     * @return
     * @throws BankRequestFailedException
     */
    @AcceptsPreStepAuth
    @PUT
    @Path("/consents/{consentId}/authorisations/{authorisationId}")
    public Response updateConsentAuthorisation(
            @NotEmpty @NotBlank @PathParam("consentId") String consentId,
            @NotEmpty @NotBlank @PathParam("authorisationId") String authorisationId,
            @Valid AuthorisationRequest authorisationRequest) throws BankRequestFailedException {
        consentId = Sanitizer.replaceEscSeq(consentId);
        authorisationId = Sanitizer.replaceEscSeq(authorisationId);
        XS2AFactoryInput xs2AFactoryInput = new XS2AFactoryInput();
        xs2AFactoryInput.setConsentId(consentId);
        xs2AFactoryInput.setPsu(getPsu());

        xs2AFactoryInput.setAuthorisationId(authorisationId);
        xs2AFactoryInput.setPsuData(authorisationRequest.getPsuData());
        xs2AFactoryInput.setAuthorisationMethodId(authorisationRequest.getAuthenticationMethodId());
        xs2AFactoryInput.setScaAuthenticationData(authorisationRequest.getScaAuthenticationData());

        IOProcessor ioProcessor = new IOProcessor(getXS2AStandard());
        ioProcessor.modifyInput(xs2AFactoryInput);

        SCA consentSCA;
        SCARequest xs2AAuthorisationRequest;

        if (authorisationRequest.getPsuData() != null) {
            xs2AAuthorisationRequest = new SCARequestFactory().create(getXS2AStandard().getRequestClassProvider().scaUpdateAuthentication(), xs2AFactoryInput);
            xs2AAuthorisationRequest.getHeaders().putAll(getAdditionalHeaders());
            ioProcessor.modifyRequest(xs2AAuthorisationRequest, xs2AFactoryInput);
            consentSCA = getXS2AStandard().getCs().updatePSUAuthentication(xs2AAuthorisationRequest);
        } else if (authorisationRequest.getAuthenticationMethodId() != null) {
            xs2AAuthorisationRequest = new SCARequestFactory().create(getXS2AStandard().getRequestClassProvider().scaUpdateAuthenticationMethod(), xs2AFactoryInput);
            xs2AAuthorisationRequest.getHeaders().putAll(getAdditionalHeaders());
            ioProcessor.modifyRequest(xs2AAuthorisationRequest, xs2AFactoryInput);
            consentSCA = getXS2AStandard().getCs().selectAuthenticationMethod(xs2AAuthorisationRequest);
        } else if (authorisationRequest.getScaAuthenticationData() != null) {
            xs2AAuthorisationRequest = new SCARequestFactory().create(getXS2AStandard().getRequestClassProvider().scaAuthoriseTransaction(), xs2AFactoryInput);
            xs2AAuthorisationRequest.getHeaders().putAll(getAdditionalHeaders());
            ioProcessor.modifyRequest(xs2AAuthorisationRequest, xs2AFactoryInput);
            consentSCA = getXS2AStandard().getCs().authoriseTransaction(xs2AAuthorisationRequest);
        } else {
            xs2AAuthorisationRequest = new SCARequestFactory().create(getXS2AStandard().getRequestClassProvider().scaUpdateIdentification(), xs2AFactoryInput);
            xs2AAuthorisationRequest.getHeaders().putAll(getAdditionalHeaders());
            ioProcessor.modifyRequest(xs2AAuthorisationRequest, xs2AFactoryInput);
            consentSCA = getXS2AStandard().getCs().updatePSUIdentification(xs2AAuthorisationRequest);
        }

        AspspUrlMapper aspspUrlMapper = new AspspUrlMapper(consentId, authorisationId);
        consentSCA.setLinks(aspspUrlMapper.map(consentSCA.getLinks()));

        LOG.info("Consent Authorisation updated for consentId={} authorisationId={} scaStatus={} scaApproach={}", consentId, authorisationId, consentSCA.getScaStatus(), consentSCA.getApproach());
        return Response.status(ResponseConstant.OK).entity(consentSCA).build();
    }

    @AcceptsPreStepAuth
    @GET
    @Path("/consents/{consentId}/authorisations/{authorisationId}")
    public Response getScaStatus(
            @NotEmpty @NotBlank @PathParam("consentId") String consentId,
            @NotEmpty @NotBlank @PathParam("authorisationId") String authorisationId) throws BankRequestFailedException {
        consentId = Sanitizer.replaceEscSeq(consentId);
        authorisationId = Sanitizer.replaceEscSeq(authorisationId);

        XS2AFactoryInput xs2AFactoryInput = new XS2AFactoryInput();
        xs2AFactoryInput.setConsentId(consentId);
        xs2AFactoryInput.setAuthorisationId(authorisationId);

        IOProcessor ioProcessor = new IOProcessor(getXS2AStandard());
        ioProcessor.modifyInput(xs2AFactoryInput);

        SCARequest getAuthorisationStatusRequest = new SCARequestFactory().create(getXS2AStandard().getRequestClassProvider().scaStatus(), xs2AFactoryInput);
        getAuthorisationStatusRequest.getHeaders().putAll(getAdditionalHeaders());

        ioProcessor.modifyRequest(getAuthorisationStatusRequest, xs2AFactoryInput);

        SCA.Status authorisationStatus = getXS2AStandard().getCs().getSCAStatus(getAuthorisationStatusRequest);
        AuthorisationStatusResponse response = new AuthorisationStatusResponse();
        response.setScaStatus(authorisationStatus.getValue());
        LOG.info("Consent Authorisation Status requested for consentId={} authorisationId={} scaStatus={}", consentId, authorisationId, authorisationStatus.getValue());
        return Response.status(ResponseConstant.OK).entity(response).build();
    }
}
