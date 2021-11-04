package net.petafuel.styx.api.v1.sad.boundary;

import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.filter.authentication.boundary.CheckAccessToken;
import net.petafuel.styx.api.rest.RestResource;
import net.petafuel.styx.api.v1.sad.control.ASPSPProcessor;
import net.petafuel.styx.api.v1.sad.entity.ASPSPResponse;
import net.petafuel.styx.spi.tokentypemapper.api.XS2ATokenType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Objects;


/**
 * Resource for aspsp data in SAD
 */
@Path("/v1")
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@CheckAccessToken(
        allowedServices = {XS2ATokenType.AISPIS, XS2ATokenType.PIS, XS2ATokenType.AIS, XS2ATokenType.PIIS},
        incrementUsage = false
)
public class SADResource extends RestResource {
    private static final Logger LOG = LogManager.getLogger(SADResource.class);

    @GET
    @Path("/aspsp/{bic}")
    public Response getAspspDataByBic(@PathParam("bic") @NotEmpty @NotBlank String bic) {
        ASPSPResponse aspspResponse = ASPSPProcessor.process(bic);

        if (Objects.isNull(aspspResponse)) {
            ResponseEntity notFoundResponse = new ResponseEntity(
                    "The requested ASPSP was not found within SAD for BIC " + bic,
                    ResponseConstant.SAD_ASPSP_NOT_FOUND,
                    ResponseCategory.ERROR,
                    ResponseOrigin.STYX
            );
            return Response.status(409).entity(notFoundResponse).build();
        } else {
            LOG.info("Successfully got bank data for bic={}", bic);
            return Response.status(200).entity(aspspResponse).build();
        }
    }
}
