package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3;

import net.petafuel.styx.core.xs2a.contracts.IXS2AHttpSigner;

/**
 * Berlin Group Signer - signs HTTP Request on Application Level
 *
 * @version 1.3
 * @see IXS2AHttpSigner
 */
//Class name shadows other Signer Classes but for different xs2a specification versions
@SuppressWarnings("squid:S2176")
public class BerlinGroupSigner extends net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.BerlinGroupSigner implements IXS2AHttpSigner {
    public BerlinGroupSigner() {
        super();
    }
}
