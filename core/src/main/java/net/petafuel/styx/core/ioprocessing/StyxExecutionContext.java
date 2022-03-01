package net.petafuel.styx.core.ioprocessing;

/**
 * Direct IOExecutionContext for independent usage within styx itself
 */
public enum StyxExecutionContext implements IOExecutionContext {
    API,
    KEEP_ALIVE;

    @Override
    public String toString(){
        return this.name();
    }
}
