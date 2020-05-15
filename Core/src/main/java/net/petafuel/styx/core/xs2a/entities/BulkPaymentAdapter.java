package net.petafuel.styx.core.xs2a.entities;

import javax.json.bind.adapter.JsonbAdapter;
import java.util.List;

public class BulkPaymentAdapter implements JsonbAdapter<List<Payment>, List<Payment>> {

    @Override
    public List<Payment> adaptToJson(List<Payment> payments) {
        //Remove debtor and requestedExecutionDate from single payment for bulk-payments
        //These values are defined once in the request, above the payment json level
        payments.forEach(payment -> {
            payment.setDebtor(null);
            payment.setRequestedExecutionDate(null);
        });

        return payments;
    }

    @Override
    public List<Payment> adaptFromJson(List<Payment> payments) {
        return payments;
    }

}
