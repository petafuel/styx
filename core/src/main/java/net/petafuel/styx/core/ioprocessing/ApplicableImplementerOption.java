package net.petafuel.styx.core.ioprocessing;

import net.petafuel.styx.core.banklookup.sad.entities.ImplementerOption;
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
     * define the logic of applying one option. This should include returning before
     * execution if this option is not
     * applicable to the current request
     *
     * @param xs2AFactoryInput inputContainer for AIS or PIS data
     * @return true or false whether this option was applied or not
     */
    public abstract boolean apply(XS2AFactoryInput xs2AFactoryInput, XS2ARequest xs2ARequest, XS2AResponse xs2AResponse)
            throws ImplementerOptionException;

    /**
     * defines where this option should be applied
     * PRE_CREATION -> before the request gets build
     * CREATION -> this option builds the request
     * POST_CREATION -> after the request was build, apply this option
     *
     * @return returns the order when this option should be applied
     */
    public abstract IOOrder order();

    /**
     * This method should return the exact String identifier use by the current
     * option
     * examples would be "IO1" or "STYX01"
     * 
     * @return the current option identifier as String
     */
    public String getOptionIdentifier() {
        return this.getClass().getSimpleName();
    }

    /**
     * Whether this option should be executed at all within more generic context
     * This will be asserted before apply is called and therefore conditions within
     * apply() will not be executed in case the IOExecutionContext is deemed
     * incorrect
     * 
     * @param ioExecutionContext
     * @return
     * @throws ImplementerOptionException in case the optionname itself could not be
     *                                    found within the io option list
     */
    public boolean shouldExecute(IOExecutionContext ioExecutionContext) throws ImplementerOptionException {
        if (StyxExecutionContext.API.name().equals(ioExecutionContext.name())) {
            return true;
        }
        // if we are within keepalive execution context, check whether the io is
        // configured to be executed here
        if (StyxExecutionContext.KEEP_ALIVE.name().equals(ioExecutionContext.name())) {
            ImplementerOption io = ioParser.get(this.getOptionIdentifier());
            if (io == null) {
                return false;
            }
            Boolean executeWithinKeepAlive = io.getOptions().getOrDefault(StyxExecutionContext.KEEP_ALIVE.name(),
                    false);
            return Boolean.TRUE.equals(executeWithinKeepAlive);
        }
        return false;
    }
}
