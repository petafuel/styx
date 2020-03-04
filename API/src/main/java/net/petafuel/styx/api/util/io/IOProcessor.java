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
import net.petafuel.styx.api.util.io.entities.STYX01;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class IOProcessor {
    private final Map<IOOrder, List<ApplicableImplementerOption<IOInputContainer>>> applicableImplementerOptionsCommon;
    private Map<IOOrder, List<ApplicableImplementerOption<IOInputContainerPIS>>> applicableImplementerOptionsPIS;
    private Map<IOOrder, List<ApplicableImplementerOption<IOInputContainerAIS>>> applicableImplementerOptionsAIS;
    private IOInputContainerPIS ioInputContainerpis;
    private IOInputContainerAIS ioInputContainerais;

    public IOProcessor(IOInputContainer ioInputContainer) {
        applicableImplementerOptionsCommon = new EnumMap<>(IOOrder.class);
        this.applicableImplementerOptionsCommon.put(IOOrder.PRE_CREATION, new ArrayList<>());
        this.applicableImplementerOptionsCommon.put(IOOrder.POST_CREATION, new ArrayList<>());
        initCommonIOs();
        if (ioInputContainer instanceof IOInputContainerPIS) {
            this.applicableImplementerOptionsPIS = new EnumMap<>(IOOrder.class);
            this.applicableImplementerOptionsPIS.put(IOOrder.PRE_CREATION, new ArrayList<>());
            this.applicableImplementerOptionsPIS.put(IOOrder.CREATION, new ArrayList<>());
            this.applicableImplementerOptionsPIS.put(IOOrder.POST_CREATION, new ArrayList<>());
            ioInputContainerpis = (IOInputContainerPIS) ioInputContainer;
            initPisIOs();
        } else if (ioInputContainer instanceof IOInputContainerAIS) {
            this.applicableImplementerOptionsAIS = new EnumMap<>(IOOrder.class);
            this.applicableImplementerOptionsAIS.put(IOOrder.PRE_CREATION, new ArrayList<>());
            this.applicableImplementerOptionsAIS.put(IOOrder.CREATION, new ArrayList<>());
            this.applicableImplementerOptionsAIS.put(IOOrder.POST_CREATION, new ArrayList<>());
            ioInputContainerais = (IOInputContainerAIS) ioInputContainer;
            initAisIOs();
        }
    }

    public XS2ARequest applyOptions() {
        XS2ARequest xs2ARequest;
        applicableImplementerOptionsCommon.get(IOOrder.PRE_CREATION).forEach(option -> option.apply(ioInputContainerpis));
        if (ioInputContainerpis != null) {
            applicableImplementerOptionsPIS.get(IOOrder.PRE_CREATION).forEach(option -> option.apply(ioInputContainerpis));
            applicableImplementerOptionsPIS.get(IOOrder.CREATION).forEach(option -> option.apply(ioInputContainerpis));
            applicableImplementerOptionsPIS.get(IOOrder.POST_CREATION).forEach(option -> option.apply(ioInputContainerpis));
            xs2ARequest = ioInputContainerpis.getPaymentRequest();
        } else {
            applicableImplementerOptionsAIS.get(IOOrder.PRE_CREATION).forEach(option -> option.apply(ioInputContainerais));
            applicableImplementerOptionsAIS.get(IOOrder.CREATION).forEach(option -> option.apply(ioInputContainerais));
            applicableImplementerOptionsAIS.get(IOOrder.POST_CREATION).forEach(option -> option.apply(ioInputContainerais));
            xs2ARequest = ioInputContainerais.getAisRequest();
        }
        applicableImplementerOptionsCommon.get(IOOrder.POST_CREATION).forEach(option -> option.apply(ioInputContainerais));
        return xs2ARequest;
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
        addPISOption(new IO2());
        addPISOption(new IO3());
        addPISOption(new IO4());
        addPISOption(new IO21());

        addPISOption(new STYX01());

    }

    /**
     * init ios for AIS requests
     */
    private void initAisIOs() {
        //Add options that apply to AIS/CS requests
    }

    private void addPISOption(ApplicableImplementerOption<IOInputContainerPIS> applicableImplementerOption) {
        applicableImplementerOptionsPIS.get(applicableImplementerOption.order()).add(applicableImplementerOption);
    }

    private void addAISOption(ApplicableImplementerOption<IOInputContainerAIS> applicableImplementerOption) {
        applicableImplementerOptionsAIS.get(applicableImplementerOption.order()).add(applicableImplementerOption);
    }

    private void addCommonOption(ApplicableImplementerOption<IOInputContainer> applicableImplementerOption) {
        applicableImplementerOptionsCommon.get(applicableImplementerOption.order()).add(applicableImplementerOption);
    }
}
