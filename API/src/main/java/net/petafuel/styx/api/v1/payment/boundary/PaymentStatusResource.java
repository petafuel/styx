package net.petafuel.styx.api.v1.payment.boundary;

import net.petafuel.styx.api.WebServer;
import net.petafuel.styx.api.filter.CheckAccessToken;
import net.petafuel.styx.api.filter.RequiresBIC;
import net.petafuel.styx.api.filter.RequiresPSU;
import net.petafuel.styx.api.rest.PSUResource;
import net.petafuel.styx.api.v1.payment.control.PaymentStatusProvider;
import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.banklookup.exceptions.BankLookupFailedException;
import net.petafuel.styx.core.banklookup.exceptions.BankNotFoundException;
import net.petafuel.styx.core.banklookup.sad.SAD;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.entities.PaymentStatus;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.ReadPaymentStatusRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v1")
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@CheckAccessToken
@RequiresPSU
public class PaymentStatusResource extends PSUResource {

    private static final Logger LOG = LogManager.getLogger(PaymentStatusResource.class);
    private PaymentStatusProvider provider = new PaymentStatusProvider();

    @HeaderParam("token")
    private String token;

    /**
     */
    @GET
    @Path("/payments/{paymentProduct}/{paymentId}/status")
    @RequiresBIC
    public Response getSinglePaymentStatus(@HeaderParam(XS2AHeader.PSU_BIC) String bic,
                                           @PathParam("paymentProduct") String paymentProduct,
                                           @PathParam("paymentId") @NotEmpty @NotBlank String paymentId
    ) throws BankLookupFailedException, BankNotFoundException, BankRequestFailedException {

        XS2AStandard xs2AStandard = (new SAD()).getBankByBIC(bic, WebServer.isSandbox());
        ReadPaymentStatusRequest request = provider.buildRequest(xs2AStandard.getAspsp(), PaymentService.PAYMENTS, "IO2", paymentProduct, paymentId);
        PaymentStatus status = xs2AStandard.getPis().getPaymentStatus(request);

        LOG.info("Successfully read the payment status entity for bic={}, paymentId={}", bic, paymentId);
        return Response.status(200).entity(status).build();
    }

    /**
     */
    @GET
    @Path("/bulk-payments/{paymentProduct}/{paymentId}/status")
    @RequiresBIC
    public Response getBulkPaymentStatus(@HeaderParam(XS2AHeader.PSU_BIC) String bic,
                                         @PathParam("paymentProduct") String paymentProduct,
                                         @PathParam("paymentId") @NotEmpty @NotBlank String paymentId
    ) throws BankLookupFailedException, BankNotFoundException, BankRequestFailedException {

        XS2AStandard xs2AStandard = (new SAD()).getBankByBIC(bic, WebServer.isSandbox());
        ReadPaymentStatusRequest request = provider.buildRequest(xs2AStandard.getAspsp(), PaymentService.BULK_PAYMENTS, "IO3", paymentProduct, paymentId);
        PaymentStatus status = xs2AStandard.getPis().getPaymentStatus(request);

        LOG.info("Successfully read the bulk-payment status entity for bic={}, paymentId={}", bic, paymentId);
        return Response.status(200).entity(status).build();
    }

    /**
     */
    @GET
    @Path("/periodic-payments/{paymentProduct}/{paymentId}/status")
    @RequiresBIC
    public Response getPeriodicPaymentStatus(@HeaderParam(XS2AHeader.PSU_BIC) String bic,
                                             @PathParam("paymentProduct") String paymentProduct,
                                             @PathParam("paymentId") @NotEmpty @NotBlank String paymentId
    ) throws BankLookupFailedException, BankNotFoundException, BankRequestFailedException {

        XS2AStandard xs2AStandard = (new SAD()).getBankByBIC(bic, WebServer.isSandbox());
        ReadPaymentStatusRequest request = provider.buildRequest(xs2AStandard.getAspsp(), PaymentService.PERIODIC_PAYMENTS, "IO4", paymentProduct, paymentId);
        PaymentStatus status = xs2AStandard.getPis().getPaymentStatus(request);

        LOG.info("Successfully read the periodic-payment status entity for bic={}, paymentId={}", bic, paymentId);
        return Response.status(200).entity(status).build();
    }

}
