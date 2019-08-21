package net.petafuel.styx.core.xs2a.entities;

import java.io.Serializable;

public class Account implements Serializable {

    private String identifier;
    private Type type;
    private String currency;
    public Account(String identifier, String currency, Type type) {
        this.identifier = identifier;
        this.currency = currency;
        this.type = type;
    }

    public Account(String identifier) {
        this(identifier, "EUR", Type.IBAN);
    }

    public Type getType() {
        return type;
    }

    public String getCurrency() {
        return currency;
    }

    public String getIdentifier() {
        return identifier;
    }

    public enum Type {
        IBAN("iban"),
        MASKED_PAN("maskedPan");

        private String jsonKey;

        Type(String str) {
            this.jsonKey = str;
        }

        public String getJsonKey() {
            return this.jsonKey;
        }
    }

}
