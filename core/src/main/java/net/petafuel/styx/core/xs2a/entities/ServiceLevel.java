package net.petafuel.styx.core.xs2a.entities;

public enum ServiceLevel {
    BKTR, //Book Transaction
    G001, //TrackedCustomerCreditTransfer
    G002, //TrackedStopAndRecall
    G003, //TrackedCorporateTransfer
    G004, //TrackedFinancialInstitutionTransfer
    NUGP, //Non-urgent Priority Payment
    NURG, //Non-urgent Payment
    PRPT, //EBAPriorityService
    SDVA, //SameDayValue
    SEPA, //SingleEuroPaymentsArea
    SVDE, //Domestic Cheque Clearing and Settlement
    URGP, //Urgent Payment
    URNS //Urgent Payment Net Settlement
}