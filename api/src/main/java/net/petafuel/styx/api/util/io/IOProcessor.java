package net.petafuel.styx.api.util.io;

import net.petafuel.styx.api.util.IOParser;
import net.petafuel.styx.api.util.io.contracts.ApplicableImplementerOption;
import net.petafuel.styx.api.util.io.contracts.IOOrder;
import net.petafuel.styx.api.util.io.entities.IO2;
import net.petafuel.styx.api.util.io.entities.IO21;
import net.petafuel.styx.api.util.io.entities.IO3;
import net.petafuel.styx.api.util.io.entities.IO4;
import net.petafuel.styx.api.util.io.entities.ImplementerOptionException;
import net.petafuel.styx.api.util.io.entities.STYX01;
import net.petafuel.styx.api.util.io.entities.STYX02;
import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class IOProcessor {
    public static final String XML_PAYMENT_PRODUCT_PREFIX = "pain.001-";
    private static final Logger LOG = LogManager.getLogger(IOProcessor.class);
    private final Map<IOOrder, List<ApplicableImplementerOption>> applicableImplementerOptions;
    private final IOParser ioParser;

    public IOProcessor(XS2AStandard xs2AStandard) {
        applicableImplementerOptions = new EnumMap<>(IOOrder.class);
        applicableImplementerOptions.put(IOOrder.PRE_CREATION, new ArrayList<>());
        applicableImplementerOptions.put(IOOrder.POST_CREATION, new ArrayList<>());
        ioParser = new IOParser(xs2AStandard.getAspsp());
        initIOs();
    }

    public IOParser getIoParser() {
        return ioParser;
    }

    public void modifyInput(XS2AFactoryInput xs2AFactoryInput) {
        applicableImplementerOptions.get(IOOrder.PRE_CREATION).forEach(option -> applySafe(option, xs2AFactoryInput, null));
    }

    public void modifyRequest(XS2ARequest xs2ARequest, XS2AFactoryInput xs2AFactoryInput) {
        applicableImplementerOptions.get(IOOrder.POST_CREATION).forEach(option -> applySafe(option, xs2AFactoryInput, xs2ARequest));
    }

    private void applySafe(ApplicableImplementerOption applicableImplementerOption, XS2AFactoryInput xs2AFactoryInput, XS2ARequest xs2ARequest) {
        try {
            applicableImplementerOption.apply(xs2AFactoryInput, xs2ARequest);
        } catch (ImplementerOptionException e) {
            LOG.warn("error applying IOs option={} order={} message={}", applicableImplementerOption.getClass().getSimpleName(), applicableImplementerOption.order(), e.getMessage());
        }
    }


    private void initIOs() {
        addOption(new IO2(ioParser));
        addOption(new IO3(ioParser));
        addOption(new IO4(ioParser));
        addOption(new IO21(ioParser));

        addOption(new STYX01(ioParser));
        addOption(new STYX02(ioParser));
    }

    private void addOption(ApplicableImplementerOption applicableImplementerOption) {
        applicableImplementerOptions.get(applicableImplementerOption.order()).add(applicableImplementerOption);
    }
}