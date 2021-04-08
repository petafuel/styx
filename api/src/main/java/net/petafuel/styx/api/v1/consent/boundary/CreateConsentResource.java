package net.petafuel.styx.api.v1.consent.boundary;

import net.petafuel.styx.api.filter.authentication.boundary.AcceptsPreStepAuth;
import net.petafuel.styx.api.filter.authentication.boundary.CheckAccessToken;
import net.petafuel.styx.api.filter.input.boundary.RequiresBIC;
import net.petafuel.styx.api.filter.input.boundary.RequiresMandatoryHeader;
import net.petafuel.styx.api.filter.input.boundary.RequiresPSU;
import net.petafuel.styx.api.ioprocessing.IOProcessor;
import net.petafuel.styx.api.rest.RestResource;
import net.petafuel.styx.api.util.AspspUrlMapper;
import net.petafuel.styx.api.v1.consent.entity.POSTConsentRequest;
import net.petafuel.styx.api.v1.consent.entity.POSTConsentResponse;
import net.petafuel.styx.core.xs2a.contracts.AISRequest;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.factory.AISRequestFactory;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;
import net.petafuel.styx.core.xs2a.sca.OAuth2;
import net.petafuel.styx.core.xs2a.sca.SCAApproach;
import net.petafuel.styx.core.xs2a.sca.SCAHandler;
import net.petafuel.styx.spi.tokentypemapper.api.XS2ATokenType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
@CheckAccessToken(allowedServices = {XS2ATokenType.AISPIS, XS2ATokenType.AIS})
@RequiresBIC
@RequiresPSU
public class CreateConsentResource extends RestResource {
    private static final Logger LOG = LogManager.getLogger(CreateConsentResource.class);

    /**
     * Creates a consent Resource on the target aspsp xs2a interface
     *
     * @param postConsentRequest must contain recurringIndicator and the AccountAccess
     * @return returns SCA related data for the consent authorisation
     * @throws BankRequestFailedException in case something goes wrong while communicating to the ASPSP interface
     */
    @RequiresMandatoryHeader
    @AcceptsPreStepAuth
    @POST
    @Path("/consents")
    public Response createConsent(@Valid @NotNull POSTConsentRequest postConsentRequest) throws BankRequestFailedException {
        Consent requestConsent = new Consent();
        requestConsent.setCombinedServiceIndicator(false);
        requestConsent.setRecurringIndicator(postConsentRequest.isRecurringIndicator());
        requestConsent.setFrequencyPerDay(4);
        requestConsent.setAccess(postConsentRequest.getAccess());

        XS2AFactoryInput xs2AFactoryInput = new XS2AFactoryInput();
        xs2AFactoryInput.setConsent(requestConsent);
        xs2AFactoryInput.setPsu(getPsu());

        IOProcessor ioProcessor = new IOProcessor(getXS2AStandard());
        ioProcessor.modifyInput(xs2AFactoryInput);

        AISRequest xs2ARequest = new AISRequestFactory().create(getXS2AStandard().getRequestClassProvider().consentCreation(), xs2AFactoryInput);
        xs2ARequest.getHeaders().putAll(getAdditionalHeaders());
        xs2ARequest.setTppRedirectPreferred(getRedirectPreferred());

        ioProcessor.modifyRequest(xs2ARequest, xs2AFactoryInput);

        Consent consent = getXS2AStandard().getCs().createConsent(xs2ARequest);

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
        postConsentResponse.setLinks(aspspUrlMapper.map(postConsentResponse.getLinks()));

        LOG.info("Created new AIS consent for bic={}", getXS2AStandard().getAspsp().getBic());
        return Response.status(Response.Status.CREATED).entity(postConsentResponse).build();
    }
}
