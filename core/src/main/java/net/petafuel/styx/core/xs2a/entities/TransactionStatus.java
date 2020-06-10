package net.petafuel.styx.core.xs2a.entities;

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

    private final String name;

    TransactionStatus(String fullName) {
        this.name = fullName;
    }

    public String getName() {
        return name;
    }

}
