package net.petafuel.styx.core.xs2a.factory;

import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;

/**
 * Factory Interface for AIS, PIS and SCA Request Factories
 *
 * @param <T> should be a abstract request parent class defining constructores for childclasses
 */
public interface XS2ARequestFactory<T extends XS2ARequest> {
    T create(Class<? extends T> providedRequest, XS2AFactoryInput factoryInput);
}
