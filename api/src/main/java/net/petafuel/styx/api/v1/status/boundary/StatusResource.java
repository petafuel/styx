package net.petafuel.styx.api.v1.status.boundary;

import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.rest.RestResource;

import javax.validation.constraints.NotBlank;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@ApplicationPath("/")
@Path("/v1/status")
@Produces({MediaType.TEXT_PLAIN})
public class StatusResource extends RestResource {
    /**
     * @param status         should be a RedirectStatus enum in lowercase
     * @param identification can be any sort of identification, e.g. oauth state or xrequest id etc.
     * @param step           optional - this can be a RedirectStep enum in lowercase
     * @return returns an empty page, the url will hold all necessary data for a client
     * @see net.petafuel.styx.api.v1.status.entity.RedirectStep
     * @see net.petafuel.styx.api.v1.status.entity.RedirectStatus
     */
    @GET
    @Path("/{status}/{identification}")
    public Response handleInternalStatusRedirect(
            @NotBlank @PathParam("status") String status,
            @NotBlank @PathParam("identification") String identification,
            @QueryParam("step") String step) {

        return Response.status(ResponseConstant.OK).entity("").build();
    }
}
