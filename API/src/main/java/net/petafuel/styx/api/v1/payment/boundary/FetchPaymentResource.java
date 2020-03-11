package net.petafuel.styx.api.v1.payment.boundary;

import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.filter.CheckAccessToken;
import net.petafuel.styx.api.filter.RequiresBIC;
import net.petafuel.styx.api.filter.RequiresPSU;
import net.petafuel.styx.api.rest.PSUResource;
import net.petafuel.styx.api.service.SADService;
import net.petafuel.styx.api.v1.payment.control.FetchPaymentProvider;
import net.petafuel.styx.api.v1.payment.entity.PaymentTypeBean;
import net.petafuel.styx.core.xs2a.entities.InitializablePayment;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.ReadPaymentRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v1")
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@Consumes({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@CheckAccessToken
@RequiresPSU
@RequiresBIC
public class FetchPaymentResource extends PSUResource {
    private static final Logger LOG = LogManager.getLogger(FetchPaymentResource.class);
    @Inject
    private SADService sadService;

    /**
     * Returns an previously initiated payment in its raw form, as json or xml pain001.003
     *
     * @param paymentTypeBean contains which payment product is used
     * @param paymentId       id of the payment that should be retrieved from the aspsp
     * @return 200 if successful
     * @throws BankRequestFailedException in case the communication between styx and aspsp was not successful
     */
    @GET
    @Path("/{paymentService}/{paymentProduct}/{paymentId}")
    public Response fetchPayment(@BeanParam PaymentTypeBean paymentTypeBean,
                                 @NotEmpty @NotBlank @PathParam("paymentId") String paymentId) throws BankRequestFailedException {
        ReadPaymentRequest aspspRequest = new FetchPaymentProvider(sadService.getXs2AStandard(), paymentTypeBean, getPsu()).buildFetchPaymentRequest(paymentId);
        aspspRequest.getHeaders().putAll(getSandboxHeaders());
        InitializablePayment fetchedPayment = sadService.getXs2AStandard().getPis().getPayment(aspspRequest);

        LOG.info("Successfully fetched payment entity for bic={}, paymentId={}", sadService.getXs2AStandard().getAspsp().getBic(), paymentId);
        return Response.status(ResponseConstant.OK).entity(fetchedPayment).build();
    }
}
