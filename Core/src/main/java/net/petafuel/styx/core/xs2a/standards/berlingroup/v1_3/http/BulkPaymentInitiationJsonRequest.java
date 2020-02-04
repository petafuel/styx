package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import net.petafuel.styx.core.xs2a.XS2APaymentInitiationRequest;
import net.petafuel.styx.core.xs2a.entities.BulkPayment;
import net.petafuel.styx.core.xs2a.entities.BulkPaymentAdapter;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.util.Optional;

public class BulkPaymentInitiationJsonRequest extends XS2APaymentInitiationRequest {
    private BulkPayment bulkPayment;

    public BulkPaymentInitiationJsonRequest(PaymentProduct paymentProduct, BulkPayment bulkPayment, PSU psu) {
        super(paymentProduct, PaymentService.BULK_PAYMENTS, psu);
        this.bulkPayment = bulkPayment;
    }

    @Override
    public Optional<String> getRawBody() {
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withAdapters(new BulkPaymentAdapter()));
        return Optional.ofNullable(jsonb.toJson(bulkPayment));
        //Gson gson = new GsonBuilder().registerTypeAdapter(this.getClass(), new BulkPaymentInitiationJsonRequestSerializer()).create();
        //return Optional.ofNullable(gson.toJson(this));
    }

    public BulkPayment getBulkPayment() {
        return bulkPayment;
    }

    public void setBulkPayment(BulkPayment bulkPayment) {
        this.bulkPayment = bulkPayment;
    }
}
