package net.petafuel.styx.api.ioprocessing;

import net.petafuel.styx.api.ioprocessing.contracts.ApplicableImplementerOption;
import net.petafuel.styx.api.ioprocessing.contracts.IOOrder;
import net.petafuel.styx.api.ioprocessing.entities.ImplementerOptionException;
import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.XS2AResponse;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class IOProcessor {
    public static final String XML_PAYMENT_PRODUCT_PREFIX = "pain.001-";
    private static final Logger LOG = LogManager.getLogger(IOProcessor.class);
    private static final String OPTIONS_PATH = "net.petafuel.styx.api.ioprocessing.options.";
    private final Map<IOOrder, List<ApplicableImplementerOption>> applicableImplementerOptions;
    private final IOParser ioParser;

    public IOProcessor(XS2AStandard xs2AStandard) {
        applicableImplementerOptions = new EnumMap<>(IOOrder.class);
        applicableImplementerOptions.put(IOOrder.PRE_CREATION, new ArrayList<>());
        applicableImplementerOptions.put(IOOrder.POST_CREATION, new ArrayList<>());
        applicableImplementerOptions.put(IOOrder.PRE_RESPONSE, new ArrayList<>());
        ioParser = new IOParser(xs2AStandard.getAspsp());
        List<String> allOptions = new ArrayList<>();
        List<String> initializedOptions = new ArrayList<>();
        List<String> uninitializedOptions = new ArrayList<>();
        ioParser.getImplementerOptions().keySet().forEach(key -> {
            if (initializeOption(key)) {
                initializedOptions.add(key);
            } else {
                uninitializedOptions.add(key);
            }
            allOptions.add(key);
        });
        String availableOptions = allOptions.toString();
        LOG.info("IOProcessor initialized option-amount={}, initializedOptions={}, uninitializedOptions={}, availableOptions={}", ioParser.getImplementerOptions().size(), initializedOptions, uninitializedOptions, availableOptions);
    }

    public IOParser getIoParser() {
        return ioParser;
    }

    public void modifyInput(XS2AFactoryInput xs2AFactoryInput) {
        applicableImplementerOptions.get(IOOrder.PRE_CREATION).forEach(option -> applySafe(option, xs2AFactoryInput, null, null));
    }

    public void modifyRequest(XS2ARequest xs2ARequest, XS2AFactoryInput xs2AFactoryInput) {
        applicableImplementerOptions.get(IOOrder.POST_CREATION).forEach(option -> applySafe(option, xs2AFactoryInput, xs2ARequest, null));
    }

    public void modifyResponse(XS2AResponse xs2AResponse, XS2AFactoryInput xs2AFactoryInput) {
        applicableImplementerOptions.get(IOOrder.PRE_RESPONSE).forEach(option -> applySafe(option, xs2AFactoryInput, null, xs2AResponse));
    }

    private void applySafe(ApplicableImplementerOption applicableImplementerOption, XS2AFactoryInput xs2AFactoryInput, XS2ARequest xs2ARequest, XS2AResponse xs2AResponse) {
        try {
            boolean isExecuted = applicableImplementerOption.apply(xs2AFactoryInput, xs2ARequest, xs2AResponse);
            LOG.info("Executed option={}, order={}, isExecuted={}", applicableImplementerOption.getClass().getSimpleName(), applicableImplementerOption.order(), isExecuted);
        } catch (ImplementerOptionException e) {
            LOG.warn("error applying IOs option={} order={}", applicableImplementerOption.getClass().getSimpleName(), applicableImplementerOption.order(), e);
        }
    }

    private boolean initializeOption(String option) {
        try {
            Class<?> applicableImplementerOptionClazz = Class.forName(OPTIONS_PATH + option);
            if (applicableImplementerOptionClazz.getSuperclass() != ApplicableImplementerOption.class) {
                return false;
            }

            Constructor<?> applicableImplementerOptionConstructor = applicableImplementerOptionClazz.getConstructor(IOParser.class);
            ApplicableImplementerOption applicableImplementerOption = (ApplicableImplementerOption) applicableImplementerOptionConstructor.newInstance(ioParser);
            addOption(applicableImplementerOption);
            return true;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            //IO / STYX Class not found
            //Constructor not found
            return false;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            //Unable to call Constructor
            LOG.warn("Found implementer option but was not able to call constructor", e);
            return false;
        }
    }

    private void addOption(ApplicableImplementerOption applicableImplementerOption) {
        applicableImplementerOptions.get(applicableImplementerOption.order()).add(applicableImplementerOption);
    }
}
