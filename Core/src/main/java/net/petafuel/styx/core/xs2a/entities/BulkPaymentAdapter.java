package net.petafuel.styx.core.xs2a.entities;

import javax.json.bind.adapter.JsonbAdapter;

public class BulkPaymentAdapter implements JsonbAdapter<Payment, Payment> {

    @Override
    public Payment adaptToJson(Payment payment) {
        //Remove debtor and requestedExecutionDate from single payment for bulk-payments
        //These values are defined once in the request, above the payment json level
        payment.setDebtor(null);
        payment.setRequestedExecutionDate(null);
        return payment;
    }

    @Override
    public Payment adaptFromJson(Payment payment) {
        return null;
    }
}
