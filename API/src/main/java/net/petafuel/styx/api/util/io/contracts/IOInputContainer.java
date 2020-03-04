package net.petafuel.styx.api.util.io.contracts;

import net.petafuel.styx.api.util.IOParser;
import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.xs2a.entities.PSU;

/**
 * Contains Data for IOProcessor to create a request
 */
public abstract class IOInputContainer {
    private IOParser ioParser;
    private PSU psu;

    protected IOInputContainer(XS2AStandard xs2AStandard, PSU psu) {
        this.ioParser = new IOParser(xs2AStandard.getAspsp());
        this.psu = psu;
    }

    public IOParser getIoParser() {
        return ioParser;
    }

    public PSU getPsu() {
        return psu;
    }
}
