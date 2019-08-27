package net.petafuel.styx.core.xs2a.entities;

import java.util.Date;

public class Transaction {
    private final Status status;
    private final Type type;
    private String transactionId;
    /**
     * Could be the name of the creditor or debtor, depending on the type of the transaction
     */
    private String name;
    /**
     * Could be the account of the creditor or debtor, depending on the type of the transaction
     */
    private Account account;
    private Currency currency;
    private float amount;
    private Date bookingDate;
    private Date valueDate;
    private String remittanceInformationUnstructured;
    private String mandateId;
    private String bankTransactionCode;

    public Transaction(String transactionId, Status status, Type type, String name, Account account, Currency currency, float amount, String remittanceInformationUnstructured) {

        this.transactionId = transactionId;
        this.status = status;
        this.type = type;
        this.name = name;
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

    public Status getStatus() {
        return status;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public enum Status {
        BOOKED, PENDING //
    }

    public enum Type {
        CREDIT, DEBIT
    }
}
