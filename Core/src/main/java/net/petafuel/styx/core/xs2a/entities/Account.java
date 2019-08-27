package net.petafuel.styx.core.xs2a.entities;

import java.io.Serializable;
import java.util.ArrayList;

public class Account implements Serializable {

    /**
     * This is the universal account identifier as iban or bban
     */
    private String identifier;
    /**
     * This is the `account-id` used by the banks
     */
    private String resourceId;
    private String name;
    private String product;
    private String cashAccountType;
    private Type type;
    private Currency currency;
    private ArrayList<Balance> balances;

    public Account(String identifier, Currency currency, Type type) {
        this.identifier = identifier;
        this.currency = currency;
        this.type = type;
        this.balances = new ArrayList<>();
    }

    public Account(String identifier) {
        this(identifier, Currency.EUR, Type.IBAN);
    }

    public Type getType() {
        return type;
    }

    public Currency getCurrency() {
        return currency;
    }

    public String getIdentifier() {
        return identifier;
    }

    public enum Type {
        IBAN("iban"),
        BBAN("bban"),
        PAN("pan"),
        MASKED_PAN("maskedPan"),
        MSISDN("msisdn");

        private String jsonKey;

        Type(String str) {
            this.jsonKey = str;
        }

        public String getJsonKey() {
            return this.jsonKey;
        }
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getCashAccountType() {
        return cashAccountType;
    }

    public void setCashAccountType(String cashAccountType) {
        this.cashAccountType = cashAccountType;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public ArrayList<Balance> getBalances() {
        return balances;
    }

    public void addBalance(Balance balance) {
        this.balances.add(balance);
    }
}
