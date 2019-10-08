package net.petafuel.styx.core.banklookup;


import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.BerlinGroupCS;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.BerlinGroupSigner;

public class SimpleBankLookUp implements BankLookUpInterface
{

    public XS2AStandard getBankByIBAN(String iban) {
        XS2AStandard standard = new XS2AStandard();
        standard.setCs(new BerlinGroupCS("https://xs2a-test.fiduciagad.de/xs2a", new BerlinGroupSigner()));
        return standard;
    }

    public XS2AStandard getBankByBIC(String bic) {
        return this.getBankByIBAN(null);
    }

}
