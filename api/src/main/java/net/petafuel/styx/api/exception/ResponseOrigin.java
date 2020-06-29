package net.petafuel.styx.api.exception;

public enum ResponseOrigin {
    //Error was returned from the aspsp/bank
    ASPSP,
    //Error happened within the Styx application
    STYX,
    //Error was caused by a client, these are "expected" errors like wrong input etc.
    CLIENT
}
