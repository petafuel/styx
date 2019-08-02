package net.petafuel.styx.utils.banklookup;

/**
 * TODO URL und Standard der Deutschen Bank hinterlegen
 */
public class SimpleBankLookUp implements BankLookUpInterface
{

    public BankInterfaceDescription getBankByIBAN(String iban)
    {
        return new BankInterfaceDescription(null, null);
    }

    public BankInterfaceDescription getBankByBIC(String bic)
    {
        return new BankInterfaceDescription(null, null);
    }
}
