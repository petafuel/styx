package net.petafuel.styx.core.xs2a.entities;

import net.petafuel.styx.core.xs2a.entities.serializers.ISODateDeserializer;
import net.petafuel.styx.core.xs2a.entities.serializers.ISODateTimeDeserializer;

import javax.json.bind.annotation.JsonbTypeDeserializer;
import java.util.Date;
import java.util.List;

public class CardTransaction {
    private String cardTransactionId;
    private String terminalId;
    @JsonbTypeDeserializer(ISODateDeserializer.class)
    private Date transactionDate;
    @JsonbTypeDeserializer(ISODateTimeDeserializer.class)
    private Date acceptorTransactionDateTime;
    @JsonbTypeDeserializer(ISODateDeserializer.class)
    private Date bookingDate;
    private Amount transactionAmount;
    private List<ReportExchangeRate> currencyExchange;
    private Amount originalAmount;
    private Amount markupFee;
    private String markupFeePercentage;
    private String cardAcceptorId;
    private Address cardAcceptorAddress;
    private String cardAcceptorPhone;
    private String merchantCategoryCode;
    private String maskedPAN;
    private String transactionDetails;
    private Boolean invoiced;
    private String proprietaryBankTransactionCode;

    public String getCardTransactionId() {
        return cardTransactionId;
    }

    public void setCardTransactionId(String cardTransactionId) {
        this.cardTransactionId = cardTransactionId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Date getAcceptorTransactionDateTime() {
        return acceptorTransactionDateTime;
    }

    public void setAcceptorTransactionDateTime(Date acceptorTransactionDateTime) {
        this.acceptorTransactionDateTime = acceptorTransactionDateTime;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Amount getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Amount transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public List<ReportExchangeRate> getCurrencyExchange() {
        return currencyExchange;
    }

    public void setCurrencyExchange(List<ReportExchangeRate> currencyExchange) {
        this.currencyExchange = currencyExchange;
    }

    public Amount getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(Amount originalAmount) {
        this.originalAmount = originalAmount;
    }

    public Amount getMarkupFee() {
        return markupFee;
    }

    public void setMarkupFee(Amount markupFee) {
        this.markupFee = markupFee;
    }

    public String getMarkupFeePercentage() {
        return markupFeePercentage;
    }

    public void setMarkupFeePercentage(String markupFeePercentage) {
        this.markupFeePercentage = markupFeePercentage;
    }

    public String getCardAcceptorId() {
        return cardAcceptorId;
    }

    public void setCardAcceptorId(String cardAcceptorId) {
        this.cardAcceptorId = cardAcceptorId;
    }

    public Address getCardAcceptorAddress() {
        return cardAcceptorAddress;
    }

    public void setCardAcceptorAddress(Address cardAcceptorAddress) {
        this.cardAcceptorAddress = cardAcceptorAddress;
    }

    public String getCardAcceptorPhone() {
        return cardAcceptorPhone;
    }

    public void setCardAcceptorPhone(String cardAcceptorPhone) {
        this.cardAcceptorPhone = cardAcceptorPhone;
    }

    public String getMerchantCategoryCode() {
        return merchantCategoryCode;
    }

    public void setMerchantCategoryCode(String merchantCategoryCode) {
        this.merchantCategoryCode = merchantCategoryCode;
    }

    public String getMaskedPAN() {
        return maskedPAN;
    }

    public void setMaskedPAN(String maskedPAN) {
        this.maskedPAN = maskedPAN;
    }

    public String getTransactionDetails() {
        return transactionDetails;
    }

    public void setTransactionDetails(String transactionDetails) {
        this.transactionDetails = transactionDetails;
    }

    public Boolean getInvoiced() {
        return invoiced;
    }

    public void setInvoiced(Boolean invoiced) {
        this.invoiced = invoiced;
    }

    public String getProprietaryBankTransactionCode() {
        return proprietaryBankTransactionCode;
    }

    public void setProprietaryBankTransactionCode(String proprietaryBankTransactionCode) {
        this.proprietaryBankTransactionCode = proprietaryBankTransactionCode;
    }
}