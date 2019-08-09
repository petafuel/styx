package net.petafuel.styx.core.banklookup;

public interface BankLookUpInterface
{
    XS2AStandard getBankByIBAN(String iban);
    XS2AStandard getBankByBIC(String bic);

}
