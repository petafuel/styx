package net.petafuel.styx.api.v1.consent.boundary;

import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.filter.CheckAccessToken;
import net.petafuel.styx.api.filter.RequiresBIC;
import net.petafuel.styx.api.rest.PSUResource;
import net.petafuel.styx.api.service.SADService;
import net.petafuel.styx.api.v1.consent.control.ConsentProvider;
import net.petafuel.styx.api.v1.consent.entity.GetConsentResponse;
import net.petafuel.styx.api.v1.consent.entity.GetConsentStatusResponse;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.GetConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.StatusConsentRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationPath("/")
@Path("/v1")
@Consumes({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@RequiresBIC
@CheckAccessToken
public class GetConsentResource extends PSUResource {

    private static final Logger LOG = LogManager.getLogger(GetConsentResource.class);

    @Inject
    private SADService sadService;

    /**
     * Returns the consent object with the corresponding accounts
     *
     * @param consentId of the target consent
     * @return a GetConsentResponse object
     * @throws BankRequestFailedException if something went wrong between the core service and the aspsp
     */
    @GET
    @Path("/consents/{consentId}")
    public Response fetchConsent(@NotEmpty @NotBlank @PathParam("consentId") String consentId) throws BankRequestFailedException {
        ConsentProvider provider = new ConsentProvider(sadService.getXs2AStandard(), getPsu());

        GetConsentRequest request = (GetConsentRequest) provider.buildFetchConsentRequest(consentId);
        Consent consent = sadService.getXs2AStandard().getCs().getConsent(request);
        GetConsentResponse response = new GetConsentResponse(consent);

        LOG.info("Successfully fetched consent entity for bic={}, consentId={}", sadService.getXs2AStandard().getAspsp().getBic(), consentId);
        return Response.status(ResponseConstant.OK).entity(response).build();
    }

    /**
     * Returns the consent status
     *
     * @param consentId of the target consent
     * @return a GetConsentStatusResponse object
     * @throws BankRequestFailedException if something went wrong between the core service and the aspsp
     */
    @GET
    @Path("/consents/{consentId}/status")
    public Response getConsentStatus(@NotEmpty @NotBlank @PathParam("consentId") String consentId) throws BankRequestFailedException {
        ConsentProvider provider = new ConsentProvider(sadService.getXs2AStandard(), getPsu());

        StatusConsentRequest request = (StatusConsentRequest) provider.getConsentStatusRequest(consentId);
        Consent.State state = sadService.getXs2AStandard().getCs().getStatus(request);
        GetConsentStatusResponse response = new GetConsentStatusResponse(state);

        LOG.info("Successfully fetched consent status entity for bic={}, consentId={}", sadService.getXs2AStandard().getAspsp().getBic(), consentId);
        return Response.status(ResponseConstant.OK).entity(response).build();
    }
}
