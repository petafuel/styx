package net.petafuel.styx.core.xs2a.entities;

import net.petafuel.styx.core.xs2a.entities.serializers.TransactionStatusTypeAdapter;

import javax.json.bind.annotation.JsonbTypeAdapter;
import java.util.Arrays;

/**
 * this is the Status of a payment, called TransactionStatus in the BerlinGroup
 */
@JsonbTypeAdapter(TransactionStatusTypeAdapter.class)
public enum TransactionStatus {

    ACCC("AcceptedSettlementCompleted"),
    ACCP("AcceptedCustomerProfile"),
    ACSC("AcceptedSettlementCompleted"),
    ACSP("AcceptedSettlementInProcess"),
    ACTC("AcceptedTechnicalValidation"),
    ACWC("AcceptedWithChange"),
    ACWP("AcceptedWithoutPosting"),
    RCVD("Received"),
    PDNG("Pending"),
    RJCT("Rejected"),
    CANC("Cancelled"),
    ACFC("AcceptedFundsChecked"),
    PATC("PartiallyAcceptedTechnicalCorrect"),
    PART("PartiallyAccepted");

    private final String jsonValue;

    TransactionStatus(String fullName) {
        this.jsonValue = fullName;
    }

    public static TransactionStatus getValue(String s) {
        return Arrays.asList(values()).parallelStream().filter(transactionStatus -> transactionStatus.name().equals(s)).findFirst().orElse(null);
    }

    public String getName() {
        return jsonValue;
    }
}
