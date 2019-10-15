package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.contracts.PISInterface;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.IBerlinGroupSigner;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PaymentInitiationPain001Request;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers.InitiatedPaymentSerializer;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.SignatureException;
import java.util.UUID;

public class BerlinGroupPIS extends BasicService implements PISInterface {

    private static final Logger LOG = LogManager.getLogger(BerlinGroupPIS.class);

    private static final String INITIATE_PAYMENT = "/v1/payments/%s";

    public BerlinGroupPIS(String url, IBerlinGroupSigner signer) {
        super(url, signer);
    }

    @Override
    public InitiatedPayment initiatePaymentPain001(XS2ARequest xs2ARequest) throws SignatureException, BankRequestFailedException {

        PaymentInitiationPain001Request request = (PaymentInitiationPain001Request) xs2ARequest;

        this.setUrl(this.url + String.format(INITIATE_PAYMENT, request.getPaymentProduct().toString()));
        this.createBody(RequestType.POST, XML, request);
        this.createHeaders(request);

        try (Response response = this.execute()) {
            String body = response.body().string();

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(InitiatedPayment.class, new InitiatedPaymentSerializer())
                    .create();
            InitiatedPayment payment = gson.fromJson(body, InitiatedPayment.class);
            payment.setxRequestId(UUID.fromString(request.getHeaders().get("x-request-id")));

            //if the sca method was not set by previously parsing the body, use the bank supplied header
            if (payment.getSca().getApproach() == null) {
                payment.getSca().setApproach(SCA.Approach.valueOf(response.header("ASPSP-SCA-Approach")));
            }
            return payment;
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }
}
