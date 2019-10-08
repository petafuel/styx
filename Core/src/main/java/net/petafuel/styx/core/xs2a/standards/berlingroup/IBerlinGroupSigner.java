package net.petafuel.styx.core.xs2a.standards.berlingroup;

import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;

public interface IBerlinGroupSigner {

    void sign(XS2ARequest xs2aRequest);
}
