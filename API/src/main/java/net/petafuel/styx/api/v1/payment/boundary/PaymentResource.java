package net.petafuel.styx.api.v1.payment.boundary;

import net.petafuel.jsepa.model.CCTInitiation;
import net.petafuel.jsepa.model.CreditTransferTransactionInformation;
import net.petafuel.jsepa.model.GroupHeader;
import net.petafuel.jsepa.model.PAIN00100303Document;
import net.petafuel.jsepa.model.PaymentInstructionInformation;
import net.petafuel.styx.api.WebServer;
import net.petafuel.styx.api.filter.CheckAccessToken;
import net.petafuel.styx.api.filter.RequiresPSU;
import net.petafuel.styx.api.rest.entity.PSUResource;
import net.petafuel.styx.api.v1.payment.boundary.entity.PaymentInitiationRequest;
import net.petafuel.styx.api.v1.payment.boundary.entity.PaymentProductBean;
import net.petafuel.styx.api.v1.payment.boundary.entity.PaymentResponse;
import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.banklookup.exceptions.BankLookupFailedException;
import net.petafuel.styx.core.banklookup.exceptions.BankNotFoundException;
import net.petafuel.styx.core.banklookup.sad.SAD;
import net.petafuel.styx.core.banklookup.sad.entities.ImplementerOption;
import net.petafuel.styx.core.persistence.layers.PersistentPayment;
import net.petafuel.styx.core.xs2a.XS2APaymentInitiationRequest;
import net.petafuel.styx.core.xs2a.entities.Payment;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.BulkPaymentInitiationJsonRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PaymentInitiationJsonRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PaymentInitiationPain001Request;
import net.petafuel.styx.core.xs2a.utils.jsepa.PmtInf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.Vector;

@Path("/v1")
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@CheckAccessToken
@RequiresPSU
public class PaymentResource extends PSUResource {

    private static final Logger LOG = LogManager.getLogger(PaymentResource.class);

    @HeaderParam("token")
    private String token;

    @POST
    @Path("/payments/{paymentProduct}")
    public Response initiateSinglePayment(@HeaderParam("bic") String bic, @BeanParam PaymentProductBean paymentProductBean, @Valid PaymentInitiationRequest paymentInitiationRequest) throws BankLookupFailedException, BankNotFoundException, BankRequestFailedException {
        XS2AStandard xs2AStandard = (new SAD()).getBankByBIC(bic, WebServer.isSandbox());
        Payment payment = paymentInitiationRequest.getPayments().get(0);
        XS2APaymentInitiationRequest singlePaymentInitiation;
        //check IO2 for xml or json
        ImplementerOption supportedSinglePaymentProduct = xs2AStandard.getAspsp().getConfig().getImplementerOptions().get("IO2");
        if (supportedSinglePaymentProduct.getOptions().get(paymentProductBean.getPaymentProduct().toString()).getAsBoolean()) {
            //aspsp accepts json
            singlePaymentInitiation = new PaymentInitiationJsonRequest(paymentProductBean.getPaymentProduct(), payment, getPsu());
        } else {
            //aspsp does not support json, use pain001.003
            PAIN00100303Document document = new PAIN00100303Document();
            CCTInitiation ccInitation = new CCTInitiation();
            GroupHeader groupHeader = new GroupHeader();
            Vector<PaymentInstructionInformation> pmtInfos = new Vector<>();
            PmtInf pii = new PmtInf();
            CreditTransferTransactionInformation cdtTrfTxInf = new CreditTransferTransactionInformation();

            // Necessary variables for creating a PAIN00100303Document
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String creationTime = simpleDateFormat.format(new Date());
            int numberOfTransactions = 1;
            double controlSum = Double.parseDouble(payment.getInstructedAmount().getAmount());
            String paymentInformationId = "NOTPROVIDED";
            String paymentMethod = "TRF";
            String debtorName = payment.getDebtor().getName();
            String chargeBearer = "SLEV";

            // Setting values for each instance
            groupHeader.setMessageId(UUID.randomUUID().toString());
            groupHeader.setCreationTime(creationTime);
            groupHeader.setNoOfTransactions(numberOfTransactions);
            groupHeader.setControlSum(controlSum);
            groupHeader.setInitiatingPartyName(debtorName);

            cdtTrfTxInf.setEndToEndID(payment.getEndToEndIdentification());
            cdtTrfTxInf.setAmount(Double.parseDouble(payment.getInstructedAmount().getAmount()));
            cdtTrfTxInf.setCreditorName(payment.getCreditorName());
            cdtTrfTxInf.setCreditorIBAN(payment.getCreditor().getIban());
            cdtTrfTxInf.setVwz(payment.getRemittanceInformationUnstructured());

            ArrayList<CreditTransferTransactionInformation> list = new ArrayList<>();
            list.add(cdtTrfTxInf);

            pii.setPmtInfId(paymentInformationId);
            pii.setPaymentMethod(paymentMethod);
            pii.setNoTxns(numberOfTransactions);
            pii.setCtrlSum(controlSum);
            pii.setDebtorName(debtorName);
            pii.setDebtorAccountIBAN(payment.getDebtor().getIban());
            pii.setDebitorBic(bic);
            pii.setChargeBearer(chargeBearer);
            pii.setCreditTransferTransactionInformationVector(list);

            pmtInfos.add(pii);
            ccInitation.setGrpHeader(groupHeader);
            ccInitation.setPmtInfos(pmtInfos);
            document.setCctInitiation(ccInitation);

            singlePaymentInitiation = new PaymentInitiationPain001Request(paymentProductBean.getPaymentProduct(), PaymentService.PAYMENTS, document, getPsu());
        }
        PaymentResponse paymentResponse = new PaymentResponse(xs2AStandard.getPis().initiatePayment(singlePaymentInitiation));
        LOG.info("Initiate single payment bic={} aspsp_name={} aspsp_id={} paymentId={}", bic, xs2AStandard.getAspsp().getName(), xs2AStandard.getAspsp().getId(), paymentResponse.getPaymentId());
        PersistentPayment.create(paymentResponse.getPaymentId(), UUID.fromString(token), bic, paymentResponse.getTransactionStatus());
        return Response.status(200).entity(paymentResponse).build();
    }

    @POST
    @Path("/bulk-payments/{paymentProduct}")
    public Response initiateBulkPayment(@HeaderParam("bic") String bic, @BeanParam PaymentProductBean paymentProductBean, @Valid PaymentInitiationRequest paymentInitiationRequest) throws BankLookupFailedException, BankNotFoundException, BankRequestFailedException {
        XS2AStandard xs2AStandard = (new SAD()).getBankByBIC(bic, WebServer.isSandbox());
        LOG.info("Initiate bulk payment bic={} aspsp_name={} aspsp_id={}", bic, xs2AStandard.getAspsp().getName(), xs2AStandard.getAspsp().getId());

        //TODO check IO3 for xml or json
        BulkPaymentInitiationJsonRequest paymentInitiationJsonRequest = new BulkPaymentInitiationJsonRequest(paymentProductBean.getPaymentProduct(), paymentInitiationRequest.getPayments(), getPsu(), false);
        PaymentResponse paymentResponse = new PaymentResponse(xs2AStandard.getPis().initiatePayment(paymentInitiationJsonRequest));

        return Response.status(200).entity(paymentResponse).build();
    }

    @POST
    @Path("/periodic-payments/{paymentProduct}")
    public Response initiatePeriodicPayment(@HeaderParam("bic") String bic, @BeanParam PaymentProductBean paymentProductBean, @Valid PaymentInitiationRequest paymentInitiationRequest) throws BankLookupFailedException, BankNotFoundException, BankRequestFailedException {
        XS2AStandard xs2AStandard = (new SAD()).getBankByBIC(bic, WebServer.isSandbox());
        LOG.info("Initiate periodic payment bic={} aspsp_name={} aspsp_id={}", bic, xs2AStandard.getAspsp().getName(), xs2AStandard.getAspsp().getId());

        //TODO check IO4 for xml or json
        PaymentInitiationJsonRequest paymentInitiationJsonRequest = new PaymentInitiationJsonRequest(paymentProductBean.getPaymentProduct(), paymentInitiationRequest.getPayments().get(0), getPsu());
        PaymentResponse paymentResponse = new PaymentResponse(xs2AStandard.getPis().initiatePayment(paymentInitiationJsonRequest));
        return Response.status(200).entity(paymentResponse).build();
    }
}
