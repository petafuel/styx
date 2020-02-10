package net.petafuel.styx.core.xs2a.entities;

import javax.json.bind.annotation.JsonbProperty;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Model to describe an Account in XS2A context
 */
public class Account implements Serializable {

    /**
     * This is the universal account identifier as iban or bban
     */
    @JsonbProperty("identifier")
    private String identifier;

    @JsonbProperty("iban")
    private String iban;
    /**
     * This is the `account-id` used by the banks
     */
    private String resourceId;
    @JsonbProperty("name")
    private String name;
    @JsonbProperty("product")
    private String product;
    @JsonbProperty("cashAccountType")
    private String cashAccountType;
    private Type type;
    private Currency currency;
    private Address address;
    private String agent;
    private ArrayList<Balance> balances;

    public Account() {}

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

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }
}
