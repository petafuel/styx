package net.petafuel.styx.api;

import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;

public class JerseyTestStyx extends JerseyTest {
    @Override
    protected TestContainerFactory getTestContainerFactory()
            throws TestContainerException {
        return new StyxTestContainerFactory();
    }
}
