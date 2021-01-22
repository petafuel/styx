package net.petafuel.styx.api.v1.payment.boundary;

import net.petafuel.styx.api.filter.AbstractTokenFilter;
import net.petafuel.styx.api.filter.AcceptsPreStepAuth;
import net.petafuel.styx.api.filter.CheckAccessToken;
import net.petafuel.styx.api.filter.RequiresBIC;
import net.petafuel.styx.api.filter.RequiresPSU;
import net.petafuel.styx.api.rest.RestResource;
import net.petafuel.styx.api.util.io.IOProcessor;
import net.petafuel.styx.api.v1.payment.entity.PaymentTypeBean;
import net.petafuel.styx.core.persistence.layers.PersistentPayment;
import net.petafuel.styx.core.xs2a.contracts.PISRequest;
import net.petafuel.styx.core.xs2a.entities.PaymentStatus;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.factory.PISRequestFactory;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;
import net.petafuel.styx.spi.tokentypemapper.api.XS2ATokenType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * PIS - Fetch payment status
 */
@Path("/v1")
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@CheckAccessToken(allowedServices = {XS2ATokenType.AISPIS, XS2ATokenType.PIS})
@RequiresPSU
@RequiresBIC
public class PaymentStatusResource extends RestResource {
    private static final Logger LOG = LogManager.getLogger(PaymentStatusResource.class);

    /**
     * Returns the status of a payment
     *
     * @param paymentTypeBean payment-service and payment-product
     * @param paymentId       id of the target payment
     * @return a PaymentStatus object
     * @throws BankRequestFailedException if something went wrong between the core service and the aspsp
     */
    @AcceptsPreStepAuth
    @GET
    @Path("/{paymentService}/{paymentProduct}/{paymentId}/status")
    public Response getSinglePaymentStatus(@BeanParam PaymentTypeBean paymentTypeBean,
                                           @PathParam("paymentId") @NotEmpty @NotBlank String paymentId
    ) throws BankRequestFailedException {
        XS2AFactoryInput xs2AFactoryInput = new XS2AFactoryInput();
        xs2AFactoryInput.setPaymentService(paymentTypeBean.getPaymentService());
        xs2AFactoryInput.setPaymentProduct(paymentTypeBean.getPaymentProduct());
        xs2AFactoryInput.setPaymentId(paymentId);
        xs2AFactoryInput.setPsu(getPsu());

        IOProcessor ioProcessor = new IOProcessor(getXS2AStandard());
        ioProcessor.modifyInput(xs2AFactoryInput);

        PISRequest readPaymentStatusRequest = new PISRequestFactory().create(getXS2AStandard().getRequestClassProvider().paymentStatus(), xs2AFactoryInput);
        readPaymentStatusRequest.getHeaders().putAll(getAdditionalHeaders());

        ioProcessor.modifyRequest(readPaymentStatusRequest, xs2AFactoryInput);
        PaymentStatus status = getXS2AStandard().getPis().getPaymentStatus(readPaymentStatusRequest);
        if (PersistentPayment.getByPaymentId(paymentId) == null) {
            PersistentPayment.create(ThreadContext.get("requestUUID"), paymentId, (String) getContainerRequestContext().getProperty(AbstractTokenFilter.class.getName()), getXS2AStandard().getAspsp().getBic(), status.getTransactionStatus());
        } else {
            PersistentPayment.updateStatusByPaymentId(paymentId, status.getTransactionStatus());
        }
        LOG.info("Successfully read the payment status entity for bic={}, paymentId={}", getXS2AStandard().getAspsp().getBic(), paymentId);
        return Response.status(200).entity(status).build();
    }
}
