package net.petafuel.styx.core.xs2a.entities;

import net.petafuel.styx.core.xs2a.entities.serializers.CashAccountTypeAdapter;

import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbTypeAdapter(CashAccountTypeAdapter.class)
public enum CashAccountType {
    //Current
    CACC,
    //CashPayment
    CASH,
    //Charges
    CHAR,
    //CashIncome
    CISH,
    //Commission
    COMM,
    //ClearingParticipantSettlementAccount
    CPAC,
    //LimitedLiquiditySavingsAccount
    LLSV,
    //Loan
    LOAN,
    //Marginal Lending
    MGLD,
    //Money Market
    MOMA,
    //NonResidentExternal
    NREX,
    //Overdraft
    ODFT,
    //OverNightDeposit
    ONDP,
    //OtherAccount
    OTHR,
    //Settlement
    SCAA,
    //Salary
    SLRY,
    //Savings
    SVGS,
    //Tax
    TAXE,
    //TransactingAccount
    TRAN,
    //Cash Trading
    TRAS
}