package net.petafuel.styx.core.xs2a.entities;

import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class Amount implements Serializable {
    @JsonbProperty("currency")
    Currency currency;

    @NotNull(message = "Amount cannot be null")
    @NotEmpty(message = "Amount cannot be empty")
    @JsonbProperty("amount")
    @SuppressWarnings("squid:S1700")
    String amount;

    /**
     * default constructor for json binding
     */
    public Amount() {
        //Default constructor for json bind
    }

    /**
     * Default currency is EUR
     *
     * @param amount float amount as string
     */
    public Amount(String amount) {
        this(amount, Currency.EUR);
    }

    public Amount(String amount, Currency currency) {
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