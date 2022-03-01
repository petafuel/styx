package net.petafuel.styx.core.xs2a.standards.ing.v1_0;

import net.petafuel.styx.core.xs2a.contracts.IXS2AHttpSigner;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.BerlinGroupAIS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class INGAIS extends BerlinGroupAIS {
    private static final Logger LOG = LogManager.getLogger(INGAIS.class);

    public INGAIS(String url, IXS2AHttpSigner signer) {
        super(LOG, url, signer);
    }
}
