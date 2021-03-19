package net.petafuel.styx.core.xs2a.utils.sepa.camt052.model;

public class TransactionSimple
{
    private String status;

    private String creditorIban;

    private String creditorBic;

    private String amount;

    private String currency;

    private String reference;

    private String debtorIban;

    private String debtorBic;


    public TransactionSimple() {
        //The empty Constructor serve the serialization purposes. Don't remove!
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreditorIban() {
        return creditorIban;
    }

    public void setCreditorIban(String creditorIban) {
        this.creditorIban = creditorIban;
    }

    public String getCreditorBic() {
        return creditorBic;
    }

    public void setCreditorBic(String creditorBic) {
        this.creditorBic = creditorBic;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getDebtorIban() {
        return debtorIban;
    }

    public void setDebtorIban(String debtorIban) {
        this.debtorIban = debtorIban;
    }

    public String getDebtorBic() {
        return debtorBic;
    }

    public void setDebtorBic(String debtorBic) {
        this.debtorBic = debtorBic;
    }
}
