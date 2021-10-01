package net.petafuel.styx.api.ioprocessing.options;

import net.petafuel.styx.api.ioprocessing.IOParser;
import net.petafuel.styx.api.ioprocessing.contracts.IOOrder;
import net.petafuel.styx.api.ioprocessing.entities.ImplementerOptionException;
import net.petafuel.styx.core.banklookup.sad.entities.Aspsp;
import net.petafuel.styx.core.banklookup.sad.entities.Config;
import net.petafuel.styx.core.banklookup.sad.entities.ImplementerOption;
import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;
import org.junit.Assume;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Collections;
import java.util.Optional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class STYX08UnitTest {
    private static final String TEST_BIC = "HYVEDEM488";
    private static Aspsp uniCredit;
    private static ImplementerOption styx08Option;

    @BeforeAll
    static void setup() {
        styx08Option = new ImplementerOption();
        styx08Option.setId("STYX08");
        uniCredit = new Aspsp();
        uniCredit.setConfig(new Config());
        uniCredit.setBic(TEST_BIC);
    }

    @Test
    void test_STYX08() throws ImplementerOptionException {
        Assume.assumeNotNull(uniCredit);
        Assume.assumeNotNull(styx08Option);

        styx08Option.setOptions(Collections.singletonMap("required", true));
        uniCredit.getConfig().setImplementerOptions(Collections.singletonMap("STYX08", styx08Option));
        IOParser ioParser = new IOParser(uniCredit);

        STYX08 styx08 = new STYX08(ioParser);

        Assertions.assertEquals(IOOrder.POST_CREATION, styx08.order());
        Assertions.assertFalse(styx08.apply(null, null, null));

        XS2ARequest xs2ARequest = new XS2ARequest() {
            @Override
            public Optional<String> getRawBody() {
                return Optional.empty();
            }

            @Override
            public BasicService.RequestType getHttpMethod() {
                return BasicService.RequestType.GET;
            }

            @Override
            public String getServicePath() {
                return "";
            }
        };

        PSU psu = new PSU("bgdemo");
        psu.setIp("1.2.3.4");
        XS2AFactoryInput xs2AFactoryInput = new XS2AFactoryInput();
        xs2AFactoryInput.setPsu(psu);

        Assertions.assertTrue(styx08.apply(xs2AFactoryInput, xs2ARequest, null));
        Assertions.assertNotNull(xs2ARequest.getHeaders().get(XS2AHeader.PSU_IP_ADDRESS));
        Assertions.assertEquals("1.2.3.4", xs2ARequest.getHeaders().get(XS2AHeader.PSU_IP_ADDRESS));
    }
}
