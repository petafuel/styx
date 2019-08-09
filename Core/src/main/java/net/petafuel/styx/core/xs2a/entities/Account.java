package net.petafuel.styx.core.xs2a.entities;

import java.io.Serializable;

public class Account implements Serializable {

    public enum Type
    {
        IBAN,
        MASKED_PAN
    }

    private String iban;
    private String identifier;
    private Type type;
    private String currency;

    public Account(String iban){
        this.iban = iban;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public Type getType()
    {
        return type;
    }

    public String getCurrency()
    {
        return currency;
    }

    public String getIBAN() {
        return iban;
    }
    
}
