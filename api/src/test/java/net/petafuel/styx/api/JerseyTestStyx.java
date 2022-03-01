package net.petafuel.styx.api;

import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;

/**
 * Overriding the getTestContainerFactory method of JerseyTest in order for us to inject a custom TestContainer through a custom TestContainerFactory
 */
public class JerseyTestStyx extends JerseyTest {
    @Override
    protected TestContainerFactory getTestContainerFactory()
            throws TestContainerException {
        return new StyxTestContainerFactory();
    }
}
