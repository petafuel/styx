package net.petafuel.styx.core.xs2a.entities;

import net.petafuel.styx.core.xs2a.entities.serializers.ISODateDeserializer;
import net.petafuel.styx.core.xs2a.entities.serializers.ISODateTimeDeserializer;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTypeDeserializer;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

public class SinglePayment implements InitializablePayment {
    @JsonbProperty("endToEndIdentification")
    private String endToEndIdentification;

    private String instructionIdentification;
    private String debtorName;

    @NotNull
    @Valid
    @JsonbProperty("debtorAccount")
    private AccountReference debtorAccount;
    private String debtorId;
    private String ultimateDebtor;
    @NotNull
    @Valid
    @JsonbProperty("instructedAmount")
    private Amount instructedAmount;
    private Currency currencyOfTransfer;
    private PaymentExchangeRate exchangeRateInformation;

    @NotNull
    @Valid
    @JsonbProperty("creditorAccount")
    private AccountReference creditorAccount;
    private String creditorAgent;
    private String creditorAgentName;

    @NotNull
    @NotEmpty
    @NotBlank
    @JsonbProperty("creditorName")
    private String creditorName;

    private String creditorId;

    @JsonbProperty("creditorAddress")
    private Address creditorAddress;

    private String creditorNameAndAddress;
    private String ultimateCreditor;
    private PurposeCode purposeCode;
    private ChargeBearer chargeBearer;
    private ServiceLevel serviceLevel;

    @JsonbProperty("remittanceInformationUnstructured")
    private String remittanceInformationUnstructured;

    private List<String> remittanceInformationUnstructuredArray;
    private Remittance remittanceInformationStructured;
    private List<Remittance> remittanceInformationStructuredArray;


    //Only in case of future dated payments, not supported by all aspsps
    @JsonbDateFormat(value = "yyyy-MM-dd")
    @JsonbProperty("requestedExecutionDate")
    @JsonbTypeDeserializer(ISODateDeserializer.class)
    private Date requestedExecutionDate;

    @JsonbTypeDeserializer(ISODateTimeDeserializer.class)
    private Date requestedExecutionTime;

    public String getEndToEndIdentification() {
        return endToEndIdentification;
    }

    public void setEndToEndIdentification(String endToEndIdentification) {
        this.endToEndIdentification = endToEndIdentification;
    }

    public String getInstructionIdentification() {
        return instructionIdentification;
    }

    public void setInstructionIdentification(String instructionIdentification) {
        this.instructionIdentification = instructionIdentification;
    }

    public String getDebtorName() {
        return debtorName;
    }

    public void setDebtorName(String debtorName) {
        this.debtorName = debtorName;
    }

    public AccountReference getDebtorAccount() {
        return debtorAccount;
    }

    public void setDebtorAccount(AccountReference debtorAccount) {
        this.debtorAccount = debtorAccount;
    }

    public String getDebtorId() {
        return debtorId;
    }

    public void setDebtorId(String debtorId) {
        this.debtorId = debtorId;
    }

    public String getUltimateDebtor() {
        return ultimateDebtor;
    }

    public void setUltimateDebtor(String ultimateDebtor) {
        this.ultimateDebtor = ultimateDebtor;
    }

    public Amount getInstructedAmount() {
        return instructedAmount;
    }

    public void setInstructedAmount(Amount instructedAmount) {
        this.instructedAmount = instructedAmount;
    }

    public Currency getCurrencyOfTransfer() {
        return currencyOfTransfer;
    }

    public void setCurrencyOfTransfer(Currency currencyOfTransfer) {
        this.currencyOfTransfer = currencyOfTransfer;
    }

    public PaymentExchangeRate getExchangeRateInformation() {
        return exchangeRateInformation;
    }

    public void setExchangeRateInformation(PaymentExchangeRate exchangeRateInformation) {
        this.exchangeRateInformation = exchangeRateInformation;
    }

    public AccountReference getCreditorAccount() {
        return creditorAccount;
    }

    public void setCreditorAccount(AccountReference creditorAccount) {
        this.creditorAccount = creditorAccount;
    }

    public String getCreditorAgent() {
        return creditorAgent;
    }

    public void setCreditorAgent(String creditorAgent) {
        this.creditorAgent = creditorAgent;
    }

    public String getCreditorAgentName() {
        return creditorAgentName;
    }

    public void setCreditorAgentName(String creditorAgentName) {
        this.creditorAgentName = creditorAgentName;
    }

    public String getCreditorName() {
        return creditorName;
    }

    public void setCreditorName(String creditorName) {
        this.creditorName = creditorName;
    }

    public String getCreditorId() {
        return creditorId;
    }

    public void setCreditorId(String creditorId) {
        this.creditorId = creditorId;
    }

    public Address getCreditorAddress() {
        return creditorAddress;
    }

    public void setCreditorAddress(Address creditorAddress) {
        this.creditorAddress = creditorAddress;
    }

    public String getCreditorNameAndAddress() {
        return creditorNameAndAddress;
    }

    public void setCreditorNameAndAddress(String creditorNameAndAddress) {
        this.creditorNameAndAddress = creditorNameAndAddress;
    }

    public String getUltimateCreditor() {
        return ultimateCreditor;
    }

    public void setUltimateCreditor(String ultimateCreditor) {
        this.ultimateCreditor = ultimateCreditor;
    }

    public PurposeCode getPurposeCode() {
        return purposeCode;
    }

    public void setPurposeCode(PurposeCode purposeCode) {
        this.purposeCode = purposeCode;
    }

    public ChargeBearer getChargeBearer() {
        return chargeBearer;
    }

    public void setChargeBearer(ChargeBearer chargeBearer) {
        this.chargeBearer = chargeBearer;
    }

    public ServiceLevel getServiceLevel() {
        return serviceLevel;
    }

    public void setServiceLevel(ServiceLevel serviceLevel) {
        this.serviceLevel = serviceLevel;
    }

    public String getRemittanceInformationUnstructured() {
        return remittanceInformationUnstructured;
    }

    public void setRemittanceInformationUnstructured(String remittanceInformationUnstructured) {
        this.remittanceInformationUnstructured = remittanceInformationUnstructured;
    }

    public List<String> getRemittanceInformationUnstructuredArray() {
        return remittanceInformationUnstructuredArray;
    }

    public void setRemittanceInformationUnstructuredArray(List<String> remittanceInformationUnstructuredArray) {
        this.remittanceInformationUnstructuredArray = remittanceInformationUnstructuredArray;
    }

    public Remittance getRemittanceInformationStructured() {
        return remittanceInformationStructured;
    }

    public void setRemittanceInformationStructured(Remittance remittanceInformationStructured) {
        this.remittanceInformationStructured = remittanceInformationStructured;
    }

    public List<Remittance> getRemittanceInformationStructuredArray() {
        return remittanceInformationStructuredArray;
    }

    public void setRemittanceInformationStructuredArray(List<Remittance> remittanceInformationStructuredArray) {
        this.remittanceInformationStructuredArray = remittanceInformationStructuredArray;
    }

    public Date getRequestedExecutionDate() {
        return requestedExecutionDate;
    }

    public void setRequestedExecutionDate(Date requestedExecutionDate) {
        this.requestedExecutionDate = requestedExecutionDate;
    }

    public Date getRequestedExecutionTime() {
        return requestedExecutionTime;
    }

    public void setRequestedExecutionTime(Date requestedExecutionTime) {
        this.requestedExecutionTime = requestedExecutionTime;
    }
}