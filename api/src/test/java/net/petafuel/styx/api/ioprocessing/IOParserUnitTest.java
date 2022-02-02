package net.petafuel.styx.api.ioprocessing;

import net.petafuel.styx.core.banklookup.sad.entities.Aspsp;
import net.petafuel.styx.core.banklookup.sad.entities.Config;
import net.petafuel.styx.core.banklookup.sad.entities.ImplementerOption;
import net.petafuel.styx.core.ioprocessing.IOParser;
import net.petafuel.styx.core.ioprocessing.ImplementerOptionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static net.petafuel.styx.core.ioprocessing.IOParser.Option.REQUIRED;

import java.util.Collections;

class IOParserUnitTest {
    private static final String TEST_BIC = "TEST7999";
    private static Aspsp sparda;
    private static ImplementerOption styx04Option;
    private static IOParser ioParser;

    @BeforeAll
    static void setup() {
        styx04Option = new ImplementerOption();
        styx04Option.setId("STYX04");
        styx04Option.setOptions(Collections.singletonMap("required", false));
        sparda = new Aspsp();
        sparda.setConfig(new Config());
        sparda.setBic(TEST_BIC);
        sparda.getConfig().setImplementerOptions(Collections.singletonMap("STYX04", styx04Option));
        ioParser = new IOParser(sparda);
    }

    @Test
    void get() throws ImplementerOptionException {

        Assertions.assertNotNull(ioParser.get("STYX04"));
        Assertions.assertFalse(ioParser.getOption("STYX04", REQUIRED));
        Assertions.assertThrows(ImplementerOptionException.class, () -> ioParser.getOption("notContained", REQUIRED));
    }

    @Test
    void getOption() {
        Assertions.assertThrows(ImplementerOptionException.class, () -> ioParser.getOption("notContained", REQUIRED));
    }

    @Test
    void getImplementerOptions() {
        Assertions.assertNotNull(ioParser.getAspsp());
        Assertions.assertEquals(TEST_BIC, ioParser.getAspsp().getBic());
    }

    @Test
    void getAspsp() {
        Assertions.assertTrue(ioParser.getImplementerOptions().containsKey("STYX04"));
    }
}