package net.petafuel.styx.api.ioprocessing.contracts;

import net.petafuel.styx.api.ioprocessing.IOParser;
import net.petafuel.styx.api.ioprocessing.entities.ImplementerOptionException;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.XS2AResponse;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;

/**
 * Interface defines an applicable Implementer Option
 * The IOProcessor will
 */
public abstract class ApplicableImplementerOption {
    protected IOParser ioParser;

    protected ApplicableImplementerOption(IOParser ioParser) {
        this.ioParser = ioParser;
    }

    /**
     * define the logic of applying one option. This should include returning before execution if this option is not
     * applicable to the current request
     *
     * @param xs2AFactoryInput inputContainer for AIS or PIS data
     * @return true or false whether this option was applied or not
     */
    public abstract boolean apply(XS2AFactoryInput xs2AFactoryInput, XS2ARequest xs2ARequest, XS2AResponse xs2AResponse) throws ImplementerOptionException;

    /**
     * defines where this option should be applied
     * PRE_CREATION -> before the request gets build
     * CREATION -> this option builds the request
     * POST_CREATION -> after the request was build, apply this option
     *
     * @return returns the order when this option should be applied
     */
    public abstract IOOrder order();
}
