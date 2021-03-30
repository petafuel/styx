package net.petafuel.styx.api.ioprocessing.contracts;

/**
 * defines the order in which stage of the request creation through the io processor an implementer option should be applied
 */
public enum IOOrder {
    PRE_CREATION,
    POST_CREATION,
    PRE_RESPONSE
}
