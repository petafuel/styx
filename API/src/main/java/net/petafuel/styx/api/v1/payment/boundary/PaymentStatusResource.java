package net.petafuel.styx.api.v1.payment.boundary;

import net.petafuel.styx.api.filter.CheckAccessToken;
import net.petafuel.styx.api.filter.RequiresBIC;
import net.petafuel.styx.api.filter.RequiresPSU;
import net.petafuel.styx.api.rest.PSUResource;
import net.petafuel.styx.api.service.SADService;
import net.petafuel.styx.api.v1.payment.control.PaymentStatusProvider;
import net.petafuel.styx.api.v1.payment.entity.PaymentTypeBean;
import net.petafuel.styx.core.xs2a.entities.PaymentStatus;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.ReadPaymentStatusRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/v1")
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@CheckAccessToken
@RequiresPSU
@RequiresBIC
public class PaymentStatusResource extends PSUResource {
    private static final Logger LOG = LogManager.getLogger(PaymentStatusResource.class);

    @Inject
    private SADService sadService;

    @HeaderParam("token")
    private String token;

    /**
     *
     */
    @GET
    @Path("/{paymentService}/{paymentProduct}/{paymentId}/status")
    public Response getSinglePaymentStatus(@BeanParam PaymentTypeBean paymentTypeBean,
                                           @PathParam("paymentId") @NotEmpty @NotBlank String paymentId
    ) throws BankRequestFailedException {
        PaymentStatusProvider provider = new PaymentStatusProvider(sadService.getXs2AStandard(), paymentTypeBean, getPsu());
        ReadPaymentStatusRequest request = provider.buildRequest(paymentId);
        PaymentStatus status = sadService.getXs2AStandard().getPis().getPaymentStatus(request);
        provider.updateStatus(paymentId, UUID.fromString(token), sadService.getXs2AStandard().getAspsp().getBic(), status.getTransactionStatus());

        LOG.info("Successfully read the payment status entity for bic={}, paymentId={}", sadService.getXs2AStandard().getAspsp().getBic(), paymentId);
        return Response.status(200).entity(status).build();
    }
}
