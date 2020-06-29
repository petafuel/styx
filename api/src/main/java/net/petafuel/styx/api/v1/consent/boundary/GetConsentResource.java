package net.petafuel.styx.api.v1.consent.boundary;

import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.filter.AcceptsPreStepAuth;
import net.petafuel.styx.api.filter.CheckAccessToken;
import net.petafuel.styx.api.filter.RequiresBIC;
import net.petafuel.styx.api.rest.RestResource;
import net.petafuel.styx.api.util.io.IOProcessor;
import net.petafuel.styx.api.v1.consent.entity.GetConsentResponse;
import net.petafuel.styx.api.v1.consent.entity.GetConsentStatusResponse;
import net.petafuel.styx.core.xs2a.contracts.AISRequest;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.factory.AISRequestFactory;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;
import net.petafuel.styx.spi.tokentypemapper.api.XS2ATokenType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
@CheckAccessToken(allowedServices = {XS2ATokenType.AISPIS, XS2ATokenType.AIS})
public class GetConsentResource extends RestResource {
    private static final Logger LOG = LogManager.getLogger(GetConsentResource.class);


    /**
     * Returns the consent object with the corresponding accounts
     *
     * @param consentId of the target consent
     * @return a GetConsentResponse object
     * @throws BankRequestFailedException if something went wrong between the core service and the aspsp
     */
    @AcceptsPreStepAuth
    @GET
    @Path("/consents/{consentId}")
    public Response fetchConsent(@NotEmpty @NotBlank @PathParam("consentId") String consentId) throws BankRequestFailedException {
        XS2AFactoryInput xs2AFactoryInput = new XS2AFactoryInput();
        xs2AFactoryInput.setConsentId(consentId);

        IOProcessor ioProcessor = new IOProcessor(getXS2AStandard());
        ioProcessor.modifyInput(xs2AFactoryInput);

        AISRequest request = new AISRequestFactory().create(getXS2AStandard().getRequestClassProvider().consentRetrieval(), xs2AFactoryInput);
        request.getHeaders().putAll(getAdditionalHeaders());

        ioProcessor.modifyRequest(request, xs2AFactoryInput);

        Consent consent = getXS2AStandard().getCs().getConsent(request);
        GetConsentResponse response = new GetConsentResponse(consent);
        LOG.info("Successfully fetched consent entity for bic={}, consentId={}", getXS2AStandard().getAspsp().getBic(), consentId);
        return Response.status(ResponseConstant.OK).entity(response).build();
    }

    /**
     * Returns the consent status
     *
     * @param consentId of the target consent
     * @return a GetConsentStatusResponse object
     * @throws BankRequestFailedException if something went wrong between the core service and the aspsp
     */
    @AcceptsPreStepAuth
    @GET
    @Path("/consents/{consentId}/status")
    public Response getConsentStatus(@NotEmpty @NotBlank @PathParam("consentId") String consentId) throws BankRequestFailedException {
        XS2AFactoryInput xs2AFactoryInput = new XS2AFactoryInput();
        xs2AFactoryInput.setConsentId(consentId);

        IOProcessor ioProcessor = new IOProcessor(getXS2AStandard());
        ioProcessor.modifyInput(xs2AFactoryInput);

        AISRequest request = new AISRequestFactory().create(getXS2AStandard().getRequestClassProvider().consentStatus(), xs2AFactoryInput);
        request.getHeaders().putAll(getAdditionalHeaders());

        ioProcessor.modifyRequest(request, xs2AFactoryInput);
        Consent.State state = getXS2AStandard().getCs().getStatus(request);
        GetConsentStatusResponse response = new GetConsentStatusResponse(state);

        LOG.info("Successfully fetched consent status entity for bic={}, consentId={}", getXS2AStandard().getAspsp().getBic(), consentId);
        return Response.status(ResponseConstant.OK).entity(response).build();
    }
}
