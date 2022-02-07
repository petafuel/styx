package net.petafuel.styx.core.ioprocessing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import net.petafuel.styx.core.banklookup.sad.entities.Aspsp;
import net.petafuel.styx.core.banklookup.sad.entities.Config;
import net.petafuel.styx.core.banklookup.sad.entities.ImplementerOption;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.XS2AResponse;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;

class ApplicableIOTest {
    enum TestEC implements IOExecutionContext{
        SOME
    }

    @Test
    void testExecutionContext() throws ImplementerOptionException{
        
        class TEST1 extends ApplicableImplementerOption{

            protected TEST1(IOParser ioParser) {
                super(ioParser);
                //TODO Auto-generated constructor stub
            }

            @Override
            public boolean apply(XS2AFactoryInput xs2aFactoryInput, XS2ARequest xs2aRequest, XS2AResponse xs2aResponse)
                    throws ImplementerOptionException {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public IOOrder order() {
                // TODO Auto-generated method stub
                return null;
            }};

            ImplementerOption test1 = new ImplementerOption();
            test1.setId("TEST1");
            Aspsp aspsp = new Aspsp();
            aspsp.setConfig(new Config());
            aspsp.setBic("TEST7999");
            test1.setOptions(Collections.singletonMap("KEEP_ALIVE", true));
            aspsp.getConfig().setImplementerOptions(Collections.singletonMap("TEST1", test1));
            IOParser ioParser = new IOParser(aspsp);

            TEST1 aio = new TEST1(ioParser);

            assertTrue(aio.shouldExecute(StyxExecutionContext.API));
            assertTrue(aio.shouldExecute(StyxExecutionContext.KEEP_ALIVE));

            aspsp.getConfig().getImplementerOptions().get("TEST1").setOptions(Collections.singletonMap("KEEP_ALIVE", false));
            assertTrue(aio.shouldExecute(StyxExecutionContext.API));
            assertFalse(aio.shouldExecute(StyxExecutionContext.KEEP_ALIVE));

            aspsp.getConfig().getImplementerOptions().get("TEST1").setOptions(Collections.singletonMap("required", false));
            assertTrue(aio.shouldExecute(StyxExecutionContext.API));
            assertFalse(aio.shouldExecute(StyxExecutionContext.KEEP_ALIVE));
            assertFalse(aio.shouldExecute(TestEC.SOME));

            aspsp.getConfig().setImplementerOptions(Collections.emptyMap());
            ioParser = new IOParser(aspsp);
            assertTrue(aio.shouldExecute(StyxExecutionContext.API));
            assertFalse(aio.shouldExecute(StyxExecutionContext.KEEP_ALIVE));
            assertFalse(aio.shouldExecute(TestEC.SOME));
            
    }
}
