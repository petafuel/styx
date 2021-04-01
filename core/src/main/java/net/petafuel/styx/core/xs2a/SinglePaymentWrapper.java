package net.petafuel.styx.core.xs2a;

import net.petafuel.styx.core.xs2a.entities.SinglePayment;

import javax.json.bind.annotation.JsonbTransient;

public class SinglePaymentWrapper extends SinglePayment {
    private SinglePayment payment;

    @JsonbTransient
    public SinglePayment getPayment() {
        return payment;
    }

    public void setPayment(SinglePayment payment) {
        this.payment = payment;
    }
}
