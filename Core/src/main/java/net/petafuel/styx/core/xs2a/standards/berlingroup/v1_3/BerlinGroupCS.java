package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3;

import net.petafuel.styx.core.xs2a.contracts.IXS2AHttpSigner;

//This ConsentService should shadow the 1.2 as it did not change vor Berlingroup version 1.3
@SuppressWarnings("squid:S2176")
public class BerlinGroupCS extends net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.BerlinGroupCS {
    public BerlinGroupCS(String url, IXS2AHttpSigner signer) {
        super(url, signer);
    }
}
