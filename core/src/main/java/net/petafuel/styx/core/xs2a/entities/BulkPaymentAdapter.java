package net.petafuel.styx.core.xs2a.entities;

import javax.json.bind.adapter.JsonbAdapter;
import java.util.List;

public class BulkPaymentAdapter implements JsonbAdapter<List<SinglePayment>, List<SinglePayment>> {

    @Override
    public List<SinglePayment> adaptToJson(List<SinglePayment> payments) {
        //Remove debtor and requestedExecutionDate from single payment for bulk-payments
        //These values are defined once in the request, above the payment json level
        payments.forEach(payment -> {
            payment.setDebtorAccount(null);
            payment.setRequestedExecutionDate(null);
        });

        return payments;
    }

    @Override
    public List<SinglePayment> adaptFromJson(List<SinglePayment> payments) {
        return payments;
    }

}
