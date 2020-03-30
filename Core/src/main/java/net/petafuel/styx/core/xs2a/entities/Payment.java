package net.petafuel.styx.core.xs2a.entities;

import net.petafuel.styx.core.xs2a.entities.serializers.ISODateDeserializer;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTypeDeserializer;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class Payment implements InitializablePayment {
    @JsonbProperty("endToEndIdentification")
    private String endToEndIdentification;

    @Valid
    @JsonbProperty("debtorAccount")
    private Account debtor;

    @NotNull
    @Valid
    @JsonbProperty("instructedAmount")
    private InstructedAmount instructedAmount;

    @NotNull
    @Valid
    @JsonbProperty("creditorAccount")
    private Account creditor;

    @NotNull
    @NotEmpty
    @NotBlank
    @JsonbProperty("creditorName")
    private String creditorName;

    @JsonbProperty("creditorAddress")
    private Address creditorAddress;

    @JsonbProperty("remittanceInformationUnstructured")
    private String remittanceInformationUnstructured;

    //Only in case of future dated payments, not supported by all aspsps
    @JsonbDateFormat(value = "yyyy-MM-dd")
    @JsonbProperty("requestedExecutionDate")
    @JsonbTypeDeserializer(ISODateDeserializer.class)
    private Date requestedExecutionDate;

    public Payment() {
        //empty ctor for json binding
    }

    public Account getCreditor() {
        return creditor;
    }

    public void setCreditor(Account creditor) {
        this.creditor = creditor;
    }

    public Account getDebtor() {
        return debtor;
    }

    public void setDebtor(Account debtor) {
        this.debtor = debtor;
    }

    public InstructedAmount getInstructedAmount() {
        return instructedAmount;
    }

    public void setInstructedAmount(InstructedAmount instructedAmount) {
        this.instructedAmount = instructedAmount;
    }

    public String getRemittanceInformationUnstructured() {
        return remittanceInformationUnstructured;
    }

    public void setRemittanceInformationUnstructured(String remittanceInformationUnstructured) {
        this.remittanceInformationUnstructured = remittanceInformationUnstructured;
    }

    public String getEndToEndIdentification() {
        return endToEndIdentification;
    }

    public void setEndToEndIdentification(String endToEndIdentification) {
        this.endToEndIdentification = endToEndIdentification;
    }

    public String getCreditorName() {
        return creditorName;
    }

    public void setCreditorName(String creditorName) {
        this.creditorName = creditorName;
    }

    public Address getCreditorAddress() {
        return creditorAddress;
    }

    public void setCreditorAddress(Address creditorAddress) {
        this.creditorAddress = creditorAddress;
    }

    public Date getRequestedExecutionDate() {
        return requestedExecutionDate;
    }

    public void setRequestedExecutionDate(Date requestedExecutionDate) {
        this.requestedExecutionDate = requestedExecutionDate;
    }
}
