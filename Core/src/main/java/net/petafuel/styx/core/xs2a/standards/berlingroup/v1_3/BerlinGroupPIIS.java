package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3;

import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.contracts.IXS2AHttpSigner;
import net.petafuel.styx.core.xs2a.contracts.PIISInterface;

public class BerlinGroupPIIS extends BasicService implements PIISInterface {
    public BerlinGroupPIIS(String url, IXS2AHttpSigner signer) {
        super(url, signer);
    }
}
