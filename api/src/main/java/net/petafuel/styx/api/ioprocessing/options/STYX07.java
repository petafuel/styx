package net.petafuel.styx.api.ioprocessing.options;

import net.petafuel.styx.core.ioprocessing.IOParser;
import net.petafuel.styx.core.ioprocessing.ApplicableImplementerOption;
import net.petafuel.styx.core.ioprocessing.IOOrder;
import net.petafuel.styx.core.ioprocessing.ImplementerOptionException;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.StrongAuthenticatableResource;
import net.petafuel.styx.core.xs2a.entities.XS2AResponse;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;
import net.petafuel.styx.keepalive.tasks.PaymentStatusPoll;
import net.petafuel.styx.keepalive.threads.ThreadManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class STYX07 extends ApplicableImplementerOption {
    private static final String IO = "STYX07";
    private static final Logger LOG = LogManager.getLogger(STYX07.class);


    public STYX07(IOParser ioParser) {
        super(ioParser);
    }

    @Override
    public boolean apply(XS2AFactoryInput xs2AFactoryInput, XS2ARequest xs2ARequest, XS2AResponse xs2AResponse) throws ImplementerOptionException {
        if (Boolean.FALSE.equals(ioParser.getOption(IO, IOParser.Option.REQUIRED))) {
            return false;
        }

        if (!(xs2AResponse instanceof StrongAuthenticatableResource)) {
            LOG.debug("Not executing, response is not StrongAuthenticatableResource");
            return false;
        }

        StrongAuthenticatableResource scaResource = (StrongAuthenticatableResource) xs2AResponse;
        ThreadManager.getInstance().queueTask(new PaymentStatusPoll(xs2AFactoryInput, ioParser.getAspsp().getBic(), scaResource.getxRequestId()));
        return true;
    }

    @Override
    public IOOrder order() {
        return IOOrder.PRE_RESPONSE;
    }


}
