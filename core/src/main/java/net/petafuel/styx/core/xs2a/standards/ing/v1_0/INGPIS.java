package net.petafuel.styx.core.xs2a.standards.ing.v1_0;

import net.petafuel.styx.core.xs2a.contracts.IXS2AHttpSigner;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.BerlinGroupPIS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class INGPIS extends BerlinGroupPIS {
    private static final Logger LOG = LogManager.getLogger(INGPIS.class);

    public INGPIS(String url, IXS2AHttpSigner signer) {
        super(LOG, url, signer);
    }
}
