package net.petafuel.styx.core.xs2a.standards.ing.v1_0;

import net.petafuel.styx.core.xs2a.contracts.IXS2AHttpSigner;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.BerlinGroupCS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class INGCS extends BerlinGroupCS {
    private static final Logger LOG = LogManager.getLogger(INGCS.class);

    public INGCS(String url, IXS2AHttpSigner signer) {
        super(LOG, url, signer);
    }
}
