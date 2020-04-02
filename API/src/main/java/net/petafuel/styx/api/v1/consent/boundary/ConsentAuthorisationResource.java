package net.petafuel.styx.api.v1.consent.boundary;

import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.filter.CheckAccessToken;
import net.petafuel.styx.api.filter.RequiresBIC;
import net.petafuel.styx.api.filter.RequiresMandatoryHeader;
import net.petafuel.styx.api.rest.PSUResource;
import net.petafuel.styx.api.service.SADService;
import net.petafuel.styx.api.v1.payment.entity.AuthorisationRequest;
import net.petafuel.styx.core.xs2a.contracts.XS2AAuthorisationRequest;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.StartAuthorisationRequest;
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
}
