package net.petafuel.styx.api.v1.payment.entity;

import net.petafuel.styx.core.xs2a.entities.SinglePayment;

import javax.json.bind.annotation.JsonbProperty;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

public class SinglePaymentInitiation {
    @NotEmpty(message = "Cannot initiate payment without payment objects")
    @JsonbProperty("payments")
    @Valid
    private List<SinglePayment> payments;

    public List<SinglePayment> getPayments() {
        return payments;
    }

    public void setPayments(List<SinglePayment> payments) {
        this.payments = payments;
    }
}
