package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.contracts.PISInterface;
import net.petafuel.styx.core.xs2a.contracts.XS2AGetRequest;
import net.petafuel.styx.core.xs2a.entities.PaymentStatus;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.IBerlinGroupSigner;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.ReadPaymentStatusRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializer.PaymentStatusSerializer;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class BerlinGroupPIS extends BasicService implements PISInterface {

    private static final String GET_PAYMENT_STATUS = "/v1/%s/%s/%s/status";
    public enum PaymentProduct {
        SEPA_CREDIT_TRANSFERS("sepa-credit-transfers"),
        INSTANT_SEPA_CREDIT_TRANSFERS("instant-sepa-credit-transfers"),
        TARGET_2_PAYMENTS("target-2-payments"),
        CROSS_BORDER_CREDIT_TRANSFERS("cross-border-credit-transfers"),
        PAIN_SEPA_CREDIT_TRANSFERS("pain.001-sepa-credit-transfers"),
        PAIN_INSTANT_SEPA_CREDIT_TRANSFERS("pain.001-instant-sepa-credit-transfers"),
        PAIN_TARGET_2_PAYMENTS("pain.001-target-2-payments"),
        PAIN_CROSS_BORDER_CREDIT_TRANSFERS("pain.001-cross-border-credit-transfers");

        private String name;
        PaymentProduct(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
    public enum PaymentService {
        PAYMENTS("payments"),
        BULK_PAYMENTS("bulk-payments"),
        PERIODIC_PAYMENTS("periodic-payments");

        private String name;
        PaymentService(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private static final Logger LOG = LogManager.getLogger(BerlinGroupPIS.class);

    public BerlinGroupPIS(String url, IBerlinGroupSigner signer) {
        super(LOG, url, signer);
    }

    @Override
    public PaymentStatus getPaymentStatus(XS2AGetRequest request) throws BankRequestFailedException {
        this.setUrl(this.url + String.format(GET_PAYMENT_STATUS,
                ((ReadPaymentStatusRequest) request).getPaymentService().getName(),
                ((ReadPaymentStatusRequest) request).getPaymentProduct().getName(),
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
