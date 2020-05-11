package net.petafuel.styx.api.util.io.contracts;

import net.petafuel.styx.api.util.IOParser;
import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.xs2a.entities.PSU;

import java.util.Map;

/**
 * Contains Data for IOProcessor to create a request
 */
public abstract class IOInputContainer {
    private final IOParser ioParser;
    private final PSU psu;
    private Map<String, String> additionalHeaders;

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

    public Map<String, String> getAdditionalHeaders() {
        return additionalHeaders;
    }

    public void setAdditionalHeaders(Map<String, String> additionalHeaders) {
        this.additionalHeaders = additionalHeaders;
    }
}
