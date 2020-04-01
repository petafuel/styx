package net.petafuel.styx.core.xs2a.entities;

import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class InstructedAmount {
    @JsonbProperty("currency")
    Currency currency;

    @NotNull(message = "InstructedAmount cannot be null")
    @NotEmpty(message = "InstructedAmount cannot be empty")
    @JsonbProperty("amount")
    String amount;


    public InstructedAmount() {
        //Default constructor for json bind
    }

    public InstructedAmount(String amount) {
        this(amount, Currency.EUR);
    }

    public InstructedAmount(String amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
