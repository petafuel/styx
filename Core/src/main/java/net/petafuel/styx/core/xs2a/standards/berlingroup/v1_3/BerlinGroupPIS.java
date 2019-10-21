package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.jsepa.exception.SEPAParsingException;
import net.petafuel.jsepa.facades.ReportConverter;
import net.petafuel.jsepa.model.pain002.TransactionReport;
import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.contracts.IBerlinGroupSigner;
import net.petafuel.styx.core.xs2a.contracts.PISInterface;
import net.petafuel.styx.core.xs2a.contracts.XS2AGetRequest;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.entities.PaymentStatus;
import net.petafuel.styx.core.xs2a.entities.Transaction;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.ReadPaymentStatusRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializer.PaymentStatusSerializer;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PaymentInitiationPain001Request;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers.InitiatedPaymentSerializer;

import java.util.UUID;

public class BerlinGroupPIS extends BasicService implements PISInterface {

    private static final Logger LOG = LogManager.getLogger(BerlinGroupPIS.class);

    private static final String INITIATE_PAYMENT = "/v1/payments/%s";
    private static final String GET_PAYMENT_STATUS = "/v1/%s/%s/%s/status";

    public BerlinGroupPIS(String url, IBerlinGroupSigner signer) {
        super(LOG, url, signer);
    }

    @Override
    public InitiatedPayment initiatePayment(XS2ARequest xs2ARequest) throws BankRequestFailedException {

        if (xs2ARequest instanceof PaymentInitiationPain001Request) {
            PaymentInitiationPain001Request request = (PaymentInitiationPain001Request) xs2ARequest;
            this.setUrl(this.url + String.format(INITIATE_PAYMENT, request.getPaymentProduct().toString()));
            this.createBody(RequestType.POST, XML, xs2ARequest);
        } else {
            // TODO Build URL and BODY for Payment initiation in JSON format
            // this.setUrl(this.url + String.format(INITIATE_PAYMENT, request.getPaymentProduct().toString()));
            this.createBody(RequestType.POST, JSON, xs2ARequest);
         }

        this.createHeaders(xs2ARequest);

        try (Response response = this.execute()) {
            String body = response.body().string();

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(InitiatedPayment.class, new InitiatedPaymentSerializer())
                    .create();
            InitiatedPayment payment = gson.fromJson(body, InitiatedPayment.class);
            payment.setxRequestId(UUID.fromString(xs2ARequest.getHeaders().get("x-request-id")));

            //if the sca method was not set by previously parsing the body, use the bank supplied header
            if (payment.getSca().getApproach() == null) {
                payment.getSca().setApproach(SCA.Approach.valueOf(response.header("ASPSP-SCA-Approach")));
            }
            return payment;
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    @Override
    public PaymentStatus getPaymentStatus(XS2AGetRequest xs2AGetRequest) throws BankRequestFailedException {

        ReadPaymentStatusRequest request = (ReadPaymentStatusRequest) xs2AGetRequest;

        this.setUrl(this.url + String.format(GET_PAYMENT_STATUS,
                request.getPaymentService().toString(),
                request.getPaymentProduct().toString(),
                request.getPaymentId()));
        this.createBody(RequestType.GET);
        if (request.getPaymentProduct().isXml()) {
            request.setHeader(XS2AHeader.ACCEPT, XML.toString());
        } else {
            request.setHeader(XS2AHeader.ACCEPT, JSON.toString());
        }
        this.createHeaders(request);
        try (Response response = this.execute()) {
            String contentType = response.headers().get("content-type");
            if (response.code() != 200 || contentType == null) {
                throwBankRequestException(response);
            }
            String responseBody = response.body().string();
            if (JSON.toString().equalsIgnoreCase(contentType)) {
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(PaymentStatus.class, new PaymentStatusSerializer())
                        .create();
                return gson.fromJson(responseBody, PaymentStatus.class);
            } else {
                ReportConverter converter = new ReportConverter();
                TransactionReport report = converter.processReport(responseBody);
                return new PaymentStatus(Transaction.Status.valueOf(report.getStatus()), null);
            }
        } catch (IOException | SEPAParsingException e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

}
