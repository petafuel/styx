package net.petafuel.styx.api.injection;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 * Binder for Jersey to make context and dependency injection available within resources
 */
public class ServiceBinder extends AbstractBinder {
    @Override
    protected void configure() {
        //In case we need CDI at some point, put it here
    }
}