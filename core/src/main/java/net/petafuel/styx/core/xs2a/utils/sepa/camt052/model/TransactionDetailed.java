package net.petafuel.styx.core.xs2a.utils.sepa.camt052.model;

public class TransactionDetailed extends TransactionSimple {
    private String debtorName;
    private String creditorName;
    private String bookingDate;
    private String valueDate;
    private String creditDebit;

    public TransactionDetailed(){
        super();
    }

    public String getDebtorName() {
        return debtorName;
    }

    public void setDebtorName(String debtorName) {
        this.debtorName = debtorName;
    }

    public String getCreditorName() {
        return creditorName;
    }

    public void setCreditorName(String creditorName) {
        this.creditorName = creditorName;
    }

    public String getBookingDate()
    {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate)
    {
        this.bookingDate = bookingDate;
    }

    public String getValueDate()
    {
        return valueDate;
    }

    public void setValueDate(String valueDate)
    {
        this.valueDate = valueDate;
    }

    public String getCreditDebit() {
        return creditDebit;
    }

    public void setCreditDebit(String creditDebit) {
        this.creditDebit = creditDebit;
    }
}
