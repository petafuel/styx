package net.petafuel.styx.spi;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public abstract class AbstractServiceProvider<T extends ServiceProviderInterface> {
    /**
     * Retrieve all registered provider classes for this Service Provider Interface
     *
     * @return ArrayList of ProviderImplementations
     */
    public List<T> providers() {
        List<T> services = new ArrayList<>();
        ServiceLoader<T> loader = ServiceLoader.load(getSPIClass());
        loader.forEach(services::add);
        return services;
    }

    /**
     * This should return the default provider class name as string
     *
     * @return full qualified class name as String
     */
    protected abstract String getDefaultProviderClassName();

    /**
     * This should return the SPI interface
     *
     * @return the SPI interface class
     */
    protected abstract Class<T> getSPIClass();

    /**
     * Without a parameter, this returns the default service implementation of this SPI
     *
     * @return default implementation
     */
    public T provider() {
        return provider(getDefaultProviderClassName());
    }

    /**
     * Get a Service implementation by a specific class name.
     *
     * @param providerName full qualified class name of the service implementation
     * @return returns the designated implementation for the providerName parameter
     * @throws ProviderNotFoundException if there is no service implementation for the provided providerName parameter
     */
    public T provider(String providerName) {
        ServiceLoader<T> loader = ServiceLoader.load(getSPIClass());
        for (T provider : loader) {
            if (providerName.equals(provider.getClass().getName())) {
                return provider;
            }
        }
        throw new ProviderNotFoundException(String.format("Unable to load implementation %s in service provider %s", providerName, this.getClass().getName()));
    }

}
