package net.petafuel.styx.api.injection;

import net.petafuel.styx.api.service.SADService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 * Binder for Jersey to make context and dependency injection available within resources
 */
public class ServiceBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(SADService.class).to(SADService.class);
    }
}