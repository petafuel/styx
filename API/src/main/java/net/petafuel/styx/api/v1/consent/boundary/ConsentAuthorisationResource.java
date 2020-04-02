package net.petafuel.styx.api.v1.consent.boundary;

import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.filter.CheckAccessToken;
import net.petafuel.styx.api.filter.RequiresBIC;
import net.petafuel.styx.api.filter.RequiresMandatoryHeader;
import net.petafuel.styx.api.rest.PSUResource;
import net.petafuel.styx.api.service.SADService;
import net.petafuel.styx.api.util.AspspUrlMapper;
import net.petafuel.styx.api.v1.payment.entity.AuthorisationRequest;
import net.petafuel.styx.core.xs2a.contracts.XS2AAuthorisationRequest;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.StartAuthorisationRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.AuthoriseTransactionRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.SelectAuthenticationMethodRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.UpdatePSUAuthenticationRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.UpdatePSUIdentificationRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.PUT;

@ApplicationPath("/")
@Path("/v1")
@Consumes({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@CheckAccessToken
@RequiresBIC
@RequiresMandatoryHeader
public class ConsentAuthorisationResource extends PSUResource {

    private static final Logger LOG = LogManager.getLogger(ConsentAuthorisationResource.class);

    @Inject
    private SADService sadService;

    /**
     * Starts a consent authorisation
     *
     * @param consentId of the target consent
     * @return a GetConsentResponse object
     * @throws BankRequestFailedException if something went wrong between the core service and the aspsp
     */
    @POST
    @Path("/consents/{consentId}/authorisations")
    public Response startConsentAuthorisation(@NotEmpty @NotBlank @PathParam("consentId") String consentId,
                                              @Valid AuthorisationRequest authorisationRequest) throws BankRequestFailedException {

        XS2AAuthorisationRequest xs2AAuthorisationRequest = new StartAuthorisationRequest(authorisationRequest.getPsuData(),
                consentId);
        xs2AAuthorisationRequest.getHeaders().putAll(getSandboxHeaders());
        if (getRedirectPreferred() != null) {
            xs2AAuthorisationRequest.setTppRedirectPreferred(getRedirectPreferred());
        }
        SCA consentSCA = sadService.getXs2AStandard().getCs().startAuthorisation(xs2AAuthorisationRequest);

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
    @PUT
    @Path("/consents/{consentId}/authorisations/{authorisationId}")
    public Response updateConsentAuthorisation(
            @NotEmpty @NotBlank @PathParam("consentId") String consentId,
            @NotEmpty @NotBlank @PathParam("authorisationId") String authorisationId,
            @Valid AuthorisationRequest authorisationRequest) throws BankRequestFailedException {

        SCA consentSCA;
        XS2AAuthorisationRequest xs2AAuthorisationRequest;

        if (authorisationRequest.getPsuData() != null) {
            xs2AAuthorisationRequest = new UpdatePSUAuthenticationRequest(getPsu(), authorisationRequest.getPsuData(), consentId, authorisationId);
            xs2AAuthorisationRequest.getHeaders().putAll(getSandboxHeaders());
            consentSCA = sadService.getXs2AStandard().getCs().updatePSUAuthentication(xs2AAuthorisationRequest);
        } else if (authorisationRequest.getAuthenticationMethodId() != null) {
            xs2AAuthorisationRequest = new SelectAuthenticationMethodRequest(authorisationRequest.getAuthenticationMethodId(), consentId, authorisationId);
            xs2AAuthorisationRequest.getHeaders().putAll(getSandboxHeaders());
            consentSCA = sadService.getXs2AStandard().getCs().selectAuthenticationMethod(xs2AAuthorisationRequest);
        } else if (authorisationRequest.getScaAuthenticationData() != null) {
            xs2AAuthorisationRequest = new AuthoriseTransactionRequest(authorisationRequest.getScaAuthenticationData(), consentId, authorisationId);
            xs2AAuthorisationRequest.getHeaders().putAll(getSandboxHeaders());
            consentSCA = sadService.getXs2AStandard().getCs().authoriseTransaction(xs2AAuthorisationRequest);
        } else {
            xs2AAuthorisationRequest = new UpdatePSUIdentificationRequest(getPsu(), consentId, authorisationId);
            xs2AAuthorisationRequest.getHeaders().putAll(getSandboxHeaders());
            consentSCA = sadService.getXs2AStandard().getCs().updatePSUIdentification(xs2AAuthorisationRequest);
        }

        AspspUrlMapper aspspUrlMapper = new AspspUrlMapper(consentId, authorisationId);
        aspspUrlMapper.map(consentSCA.getLinks());

        LOG.info("Consent Authorisation updated for consentId={} authorisationId={} scaStatus={} scaApproach={}", consentId, authorisationId, consentSCA.getScaStatus(), consentSCA.getApproach());
        return Response.status(ResponseConstant.OK).entity(consentSCA).build();
    }

}
