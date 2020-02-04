package net.petafuel.styx.core.xs2a.entities;

import javax.json.bind.adapter.JsonbAdapter;

public class BulkPaymentAdapter implements JsonbAdapter<Payment, Payment> {

    @Override
    public Payment adaptToJson(Payment payment) throws Exception {
        payment.setDebtor(null);
        return payment;
    }

    @Override
    public Payment adaptFromJson(Payment payment) throws Exception {
        return null;
    }
}
