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
     * @deprecated default ctor for json binding
     */
    @Deprecated
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

    public enum Type {
        IBAN,
        BBAN,
        PAN,
        MASKED_PAN,
        MSISDN
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AccountReference && this.iban != null) {
            return ((AccountReference) obj).iban.equals(this.iban);
        }

        return super.equals(obj);
    }
}
