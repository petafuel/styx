package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3;

import net.petafuel.styx.core.xs2a.contracts.IXS2AHttpSigner;

//AIS Service did not change between 1.2 and 1.3, 1.3 shadows 1.2
@SuppressWarnings("squid:S2176")
public class BerlinGroupAIS extends net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.BerlinGroupAIS {
    public BerlinGroupAIS(String url, IXS2AHttpSigner signer) {
        super(url, signer);
    }
}
