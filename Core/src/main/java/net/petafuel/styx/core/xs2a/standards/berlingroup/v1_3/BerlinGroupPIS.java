package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.contracts.PISInterface;
import net.petafuel.styx.core.xs2a.contracts.XS2AGetRequest;
import net.petafuel.styx.core.xs2a.entities.PaymentStatus;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.contracts.IBerlinGroupSigner;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.ReadPaymentStatusRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializer.PaymentStatusSerializer;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PaymentInitiationPain001Request;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers.InitiatedPaymentSerializer;

import java.security.SignatureException;
import java.util.UUID;

public class BerlinGroupPIS extends BasicService implements PISInterface {

    private static final Logger LOG = LogManager.getLogger(BerlinGroupPIS.class);

    private static final String INITIATE_PAYMENT = "/v1/payments/%s";
    private static final String GET_PAYMENT_STATUS = "/v1/%s/%s/%s/status";

    public BerlinGroupPIS(String url, IBerlinGroupSigner signer) {
        super(LOG, url, signer);
    }

    @Override
    public InitiatedPayment initiatePayment(XS2ARequest xs2ARequest) throws SignatureException, BankRequestFailedException {

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
    public PaymentStatus getPaymentStatus(XS2AGetRequest request) throws BankRequestFailedException {
        this.setUrl(this.url + String.format(GET_PAYMENT_STATUS,
                ((ReadPaymentStatusRequest) request).getPaymentService().toString(),
                ((ReadPaymentStatusRequest) request).getPaymentProduct().toString(),
                ((ReadPaymentStatusRequest) request).getPaymentId()));
        this.createBody(RequestType.GET);
        this.createHeaders(request);

        try (Response response = this.execute()) {
            if (response.code() != 200) {
                throwBankRequestException(response);
            }

            ResponseBody body = response.body();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(PaymentStatus.class, new PaymentStatusSerializer())
                    .create();
            return gson.fromJson(body.string(), PaymentStatus.class);

        } catch (IOException e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

}
