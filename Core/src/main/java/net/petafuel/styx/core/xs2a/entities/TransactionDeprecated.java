package net.petafuel.styx.core.xs2a.entities;

import java.util.Arrays;
import java.util.Date;

/**
 * @see Transaction
 * @deprecated use the new Transaction Model instead of this non berlingroup-compliant class
 */
@Deprecated
public class TransactionDeprecated {
    private final BookingStatus bookingStatus;
    private final Type type;
    private final Currency currency;
    private TransactionStatus status;
    private String transactionId;
    /**
     * Could be the account of the creditor or debtor, depending on the type of the transaction
     */
    private Account account;
    private float amount;
    private Date bookingDate;
    private Date valueDate;
    private String remittanceInformationUnstructured;
    private String mandateId;
    private String bankTransactionCode;

    public TransactionDeprecated(String transactionId, BookingStatus bookingStatus, Type type, Account account, Currency currency, float amount, String remittanceInformationUnstructured) {

        this.transactionId = transactionId;
        this.bookingStatus = bookingStatus;
        this.type = type;
        this.account = account;
        this.currency = currency;
        this.amount = amount;
        this.remittanceInformationUnstructured = remittanceInformationUnstructured;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public Type getType() {
        return type;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Currency getCurrency() {
        return currency;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Date getValueDate() {
        return valueDate;
    }

    public void setValueDate(Date valueDate) {
        this.valueDate = valueDate;
    }

    public String getRemittanceInformationUnstructured() {
        return remittanceInformationUnstructured;
    }

    public void setRemittanceInformationUnstructured(String remittanceInformationUnstructured) {
        this.remittanceInformationUnstructured = remittanceInformationUnstructured;
    }

    public String getMandateId() {
        return mandateId;
    }

    public void setMandateId(String mandateId) {
        this.mandateId = mandateId;
    }

    public String getBankTransactionCode() {
        return bankTransactionCode;
    }

    public void setBankTransactionCode(String bankTransactionCode) {
        this.bankTransactionCode = bankTransactionCode;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    @Deprecated
    public enum BookingStatus {
        BOOKED("booked"),
        PENDING("pending"),
        INFORMATION("information"),
        BOTH("both");

        private final String jsonValue;

        BookingStatus(String jsonValue) {
            this.jsonValue = jsonValue;
        }

        public static TransactionDeprecated.BookingStatus getValue(String s) {
            return Arrays.asList(values()).parallelStream().filter(bookingStatus -> bookingStatus.jsonValue.equals(s)).findFirst().orElse(null);
        }

        @Override
        public String toString() {
            return jsonValue;
        }
    }

    public enum Type {
        CREDIT, DEBIT
    }
}
