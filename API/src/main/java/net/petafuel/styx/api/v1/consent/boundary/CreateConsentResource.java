package net.petafuel.styx.api.v1.consent.boundary;

import net.petafuel.styx.api.filter.CheckAccessToken;
import net.petafuel.styx.api.filter.RequiresBIC;
import net.petafuel.styx.api.filter.RequiresMandatoryHeader;
import net.petafuel.styx.api.rest.PSUResource;
import net.petafuel.styx.api.service.SADService;
import net.petafuel.styx.api.util.AspspUrlMapper;
import net.petafuel.styx.api.v1.consent.entity.POSTConsentRequest;
import net.petafuel.styx.api.v1.consent.entity.POSTConsentResponse;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.sca.OAuth2;
import net.petafuel.styx.core.xs2a.sca.SCAApproach;
import net.petafuel.styx.core.xs2a.sca.SCAHandler;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.CreateConsentRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationPath("/")
@Path("/v1")
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@Consumes({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@CheckAccessToken
@RequiresBIC
public class CreateConsentResource extends PSUResource {
    private static final Logger LOG = LogManager.getLogger(CreateConsentResource.class);

    @Inject
    private SADService sadService;

    /**
     * Creates a consent Resource on the target aspsp xs2a interface
     *
     * @param postConsentRequest must contain recurringIndicator and the AccountAccess
     * @return returns SCA related data for the consent authorisation
     * @throws BankRequestFailedException in case something goes wrong while communicating to the ASPSP interface
     * @documented https://confluence.petafuel.intern/display/TOOL/Styx+Consent+Manager+-+Interface+Definition#StyxConsentManagerInterfaceDefinition-CreateConsent
     */
    @POST
    @Path("/consents")
    @RequiresMandatoryHeader
    public Response createConsent(@Valid POSTConsentRequest postConsentRequest) throws BankRequestFailedException {
        Consent requestConsent = new Consent();
        requestConsent.setCombinedServiceIndicator(false);
        requestConsent.setRecurringIndicator(postConsentRequest.isRecurringIndicator());
        requestConsent.setFrequencyPerDay(4);
        requestConsent.setAccess(postConsentRequest.getAccess());
        XS2ARequest xs2ARequest = new CreateConsentRequest(requestConsent);
        xs2ARequest.setPsu(getPsu());
        xs2ARequest.getHeaders().putAll(getSandboxHeaders());
        xs2ARequest.setTppRedirectPreferred(getRedirectPreferred());

        Consent consent = sadService.getXs2AStandard().getCs().createConsent(xs2ARequest);

        POSTConsentResponse postConsentResponse = new POSTConsentResponse();
        postConsentResponse.setConsentId(consent.getId());
        postConsentResponse.setAspspScaApproach(consent.getSca().getApproach());
        postConsentResponse.setPsuMessage(consent.getPsuMessage());
        postConsentResponse.setLinks(consent.getLinks());

        SCAApproach approach = SCAHandler.decision(consent);
        if (approach instanceof OAuth2) {
            postConsentResponse.getLinks().getScaOAuth().setUrl(((OAuth2) approach).getAuthoriseLink());
        }

        AspspUrlMapper aspspUrlMapper = new AspspUrlMapper(consent.getId(), null);
        aspspUrlMapper.map(postConsentResponse.getLinks());

        LOG.info("Created new AIS consent for bic={}", sadService.getXs2AStandard().getAspsp().getBic());
        return Response.status(Response.Status.CREATED).entity(postConsentResponse).build();
    }
}
