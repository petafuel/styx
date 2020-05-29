package net.petafuel.styx.api.v1.payment.boundary;

import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.filter.AcceptsPreStepAuth;
import net.petafuel.styx.api.filter.CheckAccessToken;
import net.petafuel.styx.api.filter.RequiresBIC;
import net.petafuel.styx.api.filter.RequiresPSU;
import net.petafuel.styx.api.rest.RestResource;
import net.petafuel.styx.api.util.io.IOProcessor;
import net.petafuel.styx.api.v1.payment.entity.PaymentTypeBean;
import net.petafuel.styx.core.persistence.models.AccessToken;
import net.petafuel.styx.core.xs2a.contracts.PISRequest;
import net.petafuel.styx.core.xs2a.entities.InitializablePayment;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.factory.PISRequestFactory;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

/**
 * @documented https://confluence.petafuel.intern/display/TOOL/Styx+PIS+-+Interface+Definition#StyxPISInterfaceDefinition-FetchPaymentObject
 */
@Path("/v1")
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@Consumes({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@CheckAccessToken(allowedServices = {AccessToken.ServiceType.AISPIS, AccessToken.ServiceType.PIS})
@RequiresPSU
@RequiresBIC
public class FetchPaymentResource extends RestResource {
    private static final Logger LOG = LogManager.getLogger(FetchPaymentResource.class);

    /**
     * Returns an previously initiated payment in its raw form, as json or xml pain001.003
     *
     * @param paymentTypeBean contains which payment product is used
     * @param paymentId       id of the payment that should be retrieved from the aspsp
     * @return 200 if successful
     * @throws BankRequestFailedException in case the communication between styx and aspsp was not successful
     */
    @AcceptsPreStepAuth
    @GET
    @Path("/{paymentService}/{paymentProduct}/{paymentId}")
    public Response fetchPayment(@BeanParam PaymentTypeBean paymentTypeBean,
                                 @NotEmpty @NotBlank @PathParam("paymentId") String paymentId) throws BankRequestFailedException {
        XS2AFactoryInput xs2AFactoryInput = new XS2AFactoryInput();
        xs2AFactoryInput.setPaymentService(paymentTypeBean.getPaymentService());
        xs2AFactoryInput.setPaymentProduct(paymentTypeBean.getPaymentProduct());
        xs2AFactoryInput.setPsu(getPsu());
        xs2AFactoryInput.setPaymentId(paymentId);

        IOProcessor ioProcessor = new IOProcessor(getXS2AStandard());
        ioProcessor.modifyInput(xs2AFactoryInput);

        PISRequest aspspRequest = new PISRequestFactory().create(getXS2AStandard().getRequestClassProvider().paymentRetrieval(), xs2AFactoryInput);
        aspspRequest.getHeaders().putAll(getAdditionalHeaders());

        ioProcessor.modifyRequest(aspspRequest, xs2AFactoryInput);

        InitializablePayment fetchedPayment = getXS2AStandard().getPis().getPayment(aspspRequest);
        LOG.info("Successfully fetched payment entity for bic={}, paymentId={}", getXS2AStandard().getAspsp().getBic(), paymentId);
        return Response.status(ResponseConstant.OK).entity(fetchedPayment).build();
    }
}
