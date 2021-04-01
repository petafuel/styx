package net.petafuel.styx.api.ioprocessing.options;

import net.petafuel.styx.api.ioprocessing.IOParser;
import net.petafuel.styx.api.ioprocessing.contracts.IOOrder;
import net.petafuel.styx.api.ioprocessing.entities.ImplementerOptionException;
import net.petafuel.styx.core.banklookup.sad.entities.Aspsp;
import net.petafuel.styx.core.banklookup.sad.entities.Config;
import net.petafuel.styx.core.banklookup.sad.entities.ImplementerOption;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import org.junit.Assume;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Collections;
import java.util.Optional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class STYX04UnitTest {
    private static final String TEST_BIC = "TEST7999";
    private static Aspsp sparda;
    private static ImplementerOption styx04Option;

    @BeforeAll
    static void setup() {
        styx04Option = new ImplementerOption();
        styx04Option.setId("STYX04");
        sparda = new Aspsp();
        sparda.setConfig(new Config());
        sparda.setBic(TEST_BIC);
    }

    @Test
    void test_STYX04_config_is_true() throws ImplementerOptionException {
        Assume.assumeNotNull(sparda);
        Assume.assumeNotNull(styx04Option);

        styx04Option.setOptions(Collections.singletonMap("required", true));
        sparda.getConfig().setImplementerOptions(Collections.singletonMap("STYX04", styx04Option));
        IOParser ioParser = new IOParser(sparda);

        STYX04 styx04 = new STYX04(ioParser);

        Assertions.assertEquals(IOOrder.POST_CREATION, styx04.order());

        XS2ARequest anonymouseRequest = new XS2ARequest() {
            @Override
            public Optional<String> getRawBody() {
                return Optional.empty();
            }

            @Override
            public String getServicePath() {
                return "";
            }
        };
        styx04.apply(null, anonymouseRequest, null);
        Assertions.assertNotNull(anonymouseRequest.getHeaders().get("X-BIC"));
        Assertions.assertEquals(TEST_BIC, anonymouseRequest.getHeaders().get("X-BIC"));
    }

    @Test
    void test_STYX04_config_is_false() throws ImplementerOptionException {
        Assume.assumeNotNull(sparda);
        Assume.assumeNotNull(styx04Option);

        styx04Option.setOptions(Collections.singletonMap("required", false));
        sparda.getConfig().setImplementerOptions(Collections.singletonMap("STYX04", styx04Option));
        IOParser ioParser = new IOParser(sparda);

        STYX04 styx04 = new STYX04(ioParser);

        Assertions.assertEquals(IOOrder.POST_CREATION, styx04.order());

        XS2ARequest anonymouseRequest = new XS2ARequest() {
            @Override
            public Optional<String> getRawBody() {
                return Optional.empty();
            }

            @Override
            public String getServicePath() {
                return "";
            }
        };
        styx04.apply(null, anonymouseRequest, null);
        Assertions.assertNull(anonymouseRequest.getHeaders().get("X-BIC"));
    }
}
