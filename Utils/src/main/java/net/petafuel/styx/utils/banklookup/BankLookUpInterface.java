package net.petafuel.styx.utils.banklookup;

public interface BankLookUpInterface
{
    BankInterfaceDescription getBankByIBAN(String iban);
    BankInterfaceDescription getBankByBIC(String bic);

}
