package net.petafuel.styx.api.util.io;

import net.petafuel.styx.api.util.io.contracts.ApplicableImplementerOption;
import net.petafuel.styx.api.util.io.contracts.IOInputContainer;
import net.petafuel.styx.api.util.io.contracts.IOInputContainerAIS;
import net.petafuel.styx.api.util.io.contracts.IOInputContainerPIS;
import net.petafuel.styx.api.util.io.contracts.IOOrder;
import net.petafuel.styx.api.util.io.entities.IO2;
import net.petafuel.styx.api.util.io.entities.IO21;
import net.petafuel.styx.api.util.io.entities.IO3;
import net.petafuel.styx.api.util.io.entities.IO4;
import net.petafuel.styx.api.util.io.entities.ImplementerOptionException;
import net.petafuel.styx.api.util.io.entities.STYX01;
import net.petafuel.styx.api.util.io.entities.STYX02;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class IOProcessor {
    private static final Logger LOG = LogManager.getLogger(IOProcessor.class);
    private final Map<IOOrder, List<ApplicableImplementerOption>> applicableImplementerOptions;
    IOInputContainer ioInputContainer;
    IOInputContainerPIS ioInputContainerPIS;
    IOInputContainerAIS ioInputContainerAIS;

    public IOProcessor(IOInputContainer ioInputContainer) {
        this.ioInputContainer = ioInputContainer;
        applicableImplementerOptions = new EnumMap<>(IOOrder.class);
        applicableImplementerOptions.put(IOOrder.PRE_CREATION, new ArrayList<>());
        applicableImplementerOptions.put(IOOrder.CREATION, new ArrayList<>());
        applicableImplementerOptions.put(IOOrder.POST_CREATION, new ArrayList<>());
        initCommonIOs();
    }

    public XS2ARequest applyOptions() {
        XS2ARequest xs2ARequest = null;
        if (ioInputContainer instanceof IOInputContainerPIS) {
            initPisIOs();
            ioInputContainerPIS = (IOInputContainerPIS) ioInputContainer;
            applicableImplementerOptions.get(IOOrder.PRE_CREATION).forEach(option -> applySafe(ioInputContainerPIS, option));
            applicableImplementerOptions.get(IOOrder.CREATION).forEach(option -> applySafe(ioInputContainerPIS, option));
            applicableImplementerOptions.get(IOOrder.POST_CREATION).forEach(option -> applySafe(ioInputContainerPIS, option));
            xs2ARequest = ioInputContainerPIS.getPaymentRequest();
        } else if (ioInputContainer instanceof IOInputContainerAIS) {
            initAisIOs();
            ioInputContainerAIS = (IOInputContainerAIS) ioInputContainer;
            applicableImplementerOptions.get(IOOrder.PRE_CREATION).forEach(option -> applySafe(ioInputContainerAIS, option));
            applicableImplementerOptions.get(IOOrder.CREATION).forEach(option -> applySafe(ioInputContainerAIS, option));
            applicableImplementerOptions.get(IOOrder.POST_CREATION).forEach(option -> applySafe(ioInputContainerAIS, option));
            xs2ARequest = ioInputContainerAIS.getAisRequest();
        }

        return xs2ARequest;
    }

    private void applySafe(IOInputContainerAIS ioInput, ApplicableImplementerOption applicableImplementerOption) {
        try {
            ioInputContainerAIS = (IOInputContainerAIS) applicableImplementerOption.apply(ioInput);
        } catch (ImplementerOptionException e) {
            LOG.warn("error applying IOs option={} order={} message={}", applicableImplementerOption.getClass().getSimpleName(), applicableImplementerOption.order(), e.getMessage());
        }
    }

    private void applySafe(IOInputContainerPIS ioInput, ApplicableImplementerOption applicableImplementerOption) {
        try {
            ioInputContainerPIS = (IOInputContainerPIS) applicableImplementerOption.apply(ioInput);
        } catch (ImplementerOptionException e) {
            LOG.warn("error applying implementer option {}: {}", applicableImplementerOption.getClass().getSimpleName(), e.getMessage());
        }
    }

    /**
     * Initiate ios for all requests
     */
    private void initCommonIOs() {
        //Add options that apply to all requests
    }

    /**
     * init ios for PIS requests
     */
    private void initPisIOs() {
        addOption(new IO2());
        addOption(new IO3());
        addOption(new IO4());
        addOption(new IO21());

        addOption(new STYX01());
        addOption(new STYX02());
    }

    /**
     * init ios for AIS requests
     */
    private void initAisIOs() {
        //Add options that apply to AIS/CS requests
        addOption(new STYX02());
    }

    private void addOption(ApplicableImplementerOption applicableImplementerOption) {
        applicableImplementerOptions.get(applicableImplementerOption.order()).add(applicableImplementerOption);
    }

    public IOInputContainer getIoInputContainer() {
        return ioInputContainer;
    }

    public void setIoInputContainer(IOInputContainer ioInputContainer) {
        this.ioInputContainer = ioInputContainer;
    }

    public IOInputContainerPIS getIoInputContainerPIS() {
        return ioInputContainerPIS;
    }

    public void setIoInputContainerPIS(IOInputContainerPIS ioInputContainerPIS) {
        this.ioInputContainerPIS = ioInputContainerPIS;
    }

    public IOInputContainerAIS getIoInputContainerAIS() {
        return ioInputContainerAIS;
    }

    public void setIoInputContainerAIS(IOInputContainerAIS ioInputContainerAIS) {
        this.ioInputContainerAIS = ioInputContainerAIS;
    }
}
