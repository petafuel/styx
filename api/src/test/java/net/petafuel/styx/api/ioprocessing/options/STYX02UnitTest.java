package net.petafuel.styx.api.ioprocessing.options;

import net.petafuel.styx.core.banklookup.sad.entities.Aspsp;
import net.petafuel.styx.core.banklookup.sad.entities.Config;
import net.petafuel.styx.core.banklookup.sad.entities.ImplementerOption;
import net.petafuel.styx.core.ioprocessing.IOParser;
import net.petafuel.styx.core.ioprocessing.IOOrder;
import net.petafuel.styx.core.ioprocessing.ImplementerOptionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;

class STYX02UnitTest {
    private static final String TEST_BIC = "TEST7999";
    private static Aspsp aspsp;
    private static ImplementerOption styx02Option;

    @BeforeAll
    static void setup() {
        styx02Option = new ImplementerOption();
        styx02Option.setId("STYX02");
        aspsp = new Aspsp();
        aspsp.setConfig(new Config());
        aspsp.setBic(TEST_BIC);
    }

    @Test
    void testApplyNotRequired() throws ImplementerOptionException {
        styx02Option.setOptions(Collections.singletonMap("required", false));
        aspsp.getConfig().setImplementerOptions(new HashMap<>());
        aspsp.getConfig().getImplementerOptions().put("STYX02", styx02Option);

        ImplementerOption io6 = new ImplementerOption();
        io6.setId("IO6");
        io6.setOptions(Collections.singletonMap("required", false));
        aspsp.getConfig().getImplementerOptions().put("IO6", io6);


        IOParser ioParser = new IOParser(aspsp);
        STYX02 styx02 = new STYX02(ioParser);
        Assertions.assertEquals(IOOrder.POST_CREATION, styx02.order());
        Assertions.assertFalse(styx02.apply(null, null, null));
    }
}