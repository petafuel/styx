package net.petafuel.styx.core.xs2a.entities;

import javax.json.bind.annotation.JsonbTransient;

/**
 * Representation of an superficial XS2AAccount
 * This Account Container does not hold details about the account, only identifiers
 */
public class AccountReference {
    private String iban;
    private String bban;
    private String pan;
    private String maskedPan;
    private String msisdn;

    private Currency currency;
    @JsonbTransient
    private Type type;

    /**
     * Noncompliant optional field for compatibility reasons
     */
    private String name;

    public AccountReference(String identifier, Type type) {
        this.currency = Currency.EUR;
        this.type = type;
        switch (type) {
            case IBAN:
                iban = identifier;
                break;
            case BBAN:
                bban = identifier;
                break;
            case PAN:
                pan = identifier;
                break;
            case MASKED_PAN:
                maskedPan = identifier;
                break;
            case MSISDN:
                msisdn = identifier;
                break;
        }
    }

    /**
     * default ctor for json binding
     */
    public AccountReference() {
        // default ctor for json binding
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
        this.type = Type.IBAN;
    }

    public String getBban() {
        return bban;
    }

    public void setBban(String bban) {
        this.bban = bban;
        this.type = Type.BBAN;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
        this.type = Type.PAN;
    }

    public String getMaskedPan() {
        return maskedPan;
    }

    public void setMaskedPan(String maskedPan) {
        this.maskedPan = maskedPan;
        this.type = Type.MASKED_PAN;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
        this.type = Type.MSISDN;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Type getType() {
        return type;
    }

    public String getIdentifier(Type type) {
        if (type == Type.BBAN) {
            return getBban();
        } else if (type == Type.PAN) {
            return getPan();
        } else if (type == Type.MASKED_PAN) {
            return getMaskedPan();
        } else if (type == Type.MSISDN) {
            return getMsisdn();
        } else {
            return getIban();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AccountReference && ((AccountReference) obj).getType() == this.getType()) {
            return ((AccountReference) obj).getIdentifier(((AccountReference) obj).getType()).equals(this.getIdentifier(this.getType()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.getIdentifier(this.getType()).hashCode();
    }

    public enum Type {
        IBAN,
        BBAN,
        PAN,
        MASKED_PAN,
        MSISDN
    }
}
