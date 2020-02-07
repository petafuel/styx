package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2;

import net.petafuel.styx.core.xs2a.contracts.IXS2AHttpSigner;

/**supressing warning because of PIS 1.2 is the same as 1.3**/
@SuppressWarnings("squid:S2176")
public class BerlinGroupPIS extends net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.BerlinGroupPIS {
    public BerlinGroupPIS(String url, IXS2AHttpSigner signer) {
        super(url, signer);
    }
}
