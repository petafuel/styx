package net.petafuel.styx.api.util.io.contracts;

/**
 * Interface defines an applicable Implementer Option
 * The IOProcessor will
 * @param <T>
 */
public interface ApplicableImplementerOption<T extends IOInputContainer> {
    /**
     * define the logic of applying one option. This should include returning before execution if this option is not
     * applicable to the current request
     * @param ioInputContainer inputContainer for AIS or PIS data
     */
    void apply(T ioInputContainer);

    /**
     * defines where this option should be applied
     * PRE_CREATION -> before the request gets build
     * CREATION -> this option builds the request
     * POST_CREATION -> after the request was build, apply this option
     * @return returns the order when this option should be applied
     */
    IOOrder order();
}
