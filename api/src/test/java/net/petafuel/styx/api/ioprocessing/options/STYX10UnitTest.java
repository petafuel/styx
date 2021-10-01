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
class STYX10UnitTest {
    private static final String TEST_BIC_DE = "HYVEDEM488";
    private static final String TEST_BIC_AT = "BKAUATWW";
    private static final String TEST_BIC_UK = "BKAUUKWW";
    private static final String TEST_BIC_INVALID = "BKAU";
    private static Aspsp uniCredit;
    private static ImplementerOption styx10Option;

    @BeforeAll
    static void setup() {
        styx10Option = new ImplementerOption();
        styx10Option.setId("STYX10");
        uniCredit = new Aspsp();
        uniCredit.setConfig(new Config());
    }

    @Test
    void test_STYX10_with_german_bank() throws ImplementerOptionException {
        uniCredit.setBic(TEST_BIC_DE);

        Assume.assumeNotNull(uniCredit);
        Assume.assumeNotNull(styx10Option);

        styx10Option.setOptions(Collections.singletonMap("required", true));
        uniCredit.getConfig().setImplementerOptions(Collections.singletonMap("STYX10", styx10Option));
        IOParser ioParser = new IOParser(uniCredit);

        STYX10 styx10 = new STYX10(ioParser);

        Assertions.assertEquals(IOOrder.POST_CREATION, styx10.order());
        Assertions.assertFalse(styx10.apply(null, null, null));

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
        XS2AFactoryInput xs2AFactoryInput = new XS2AFactoryInput();
        xs2AFactoryInput.setPsu(psu);

        Assertions.assertTrue(styx10.apply(xs2AFactoryInput, xs2ARequest, null));
        Assertions.assertNotNull(xs2ARequest.getHeaders().get(XS2AHeader.PSU_ID_TYPE));
        Assertions.assertEquals("HVB_ONLINEBANKING", xs2ARequest.getHeaders().get(XS2AHeader.PSU_ID_TYPE));
    }

    @Test
    void test_STYX10_with_austrian_bank() throws ImplementerOptionException {
        uniCredit.setBic(TEST_BIC_AT);

        Assume.assumeNotNull(uniCredit);
        Assume.assumeNotNull(styx10Option);

        styx10Option.setOptions(Collections.singletonMap("required", true));
        uniCredit.getConfig().setImplementerOptions(Collections.singletonMap("STYX10", styx10Option));
        IOParser ioParser = new IOParser(uniCredit);

        STYX10 styx10 = new STYX10(ioParser);

        Assertions.assertEquals(IOOrder.POST_CREATION, styx10.order());
        Assertions.assertFalse(styx10.apply(null, null, null));

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
        XS2AFactoryInput xs2AFactoryInput = new XS2AFactoryInput();
        xs2AFactoryInput.setPsu(psu);

        Assertions.assertTrue(styx10.apply(xs2AFactoryInput, xs2ARequest, null));
        Assertions.assertNotNull(xs2ARequest.getHeaders().get(XS2AHeader.PSU_ID_TYPE));
        Assertions.assertEquals("24YOU", xs2ARequest.getHeaders().get(XS2AHeader.PSU_ID_TYPE));
    }

    @Test
    void test_STYX10_with_uk_bank() throws ImplementerOptionException {
        uniCredit.setBic(TEST_BIC_UK);

        Assume.assumeNotNull(uniCredit);
        Assume.assumeNotNull(styx10Option);

        styx10Option.setOptions(Collections.singletonMap("required", true));
        uniCredit.getConfig().setImplementerOptions(Collections.singletonMap("STYX10", styx10Option));
        IOParser ioParser = new IOParser(uniCredit);

        STYX10 styx10 = new STYX10(ioParser);

        Assertions.assertEquals(IOOrder.POST_CREATION, styx10.order());
        Assertions.assertFalse(styx10.apply(null, null, null));

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
        XS2AFactoryInput xs2AFactoryInput = new XS2AFactoryInput();
        xs2AFactoryInput.setPsu(psu);

        Assertions.assertFalse(styx10.apply(xs2AFactoryInput, xs2ARequest, null));
    }

    @Test
    void test_STYX10_with_invalid_bic() throws ImplementerOptionException {
        uniCredit.setBic(TEST_BIC_INVALID);

        Assume.assumeNotNull(uniCredit);
        Assume.assumeNotNull(styx10Option);

        styx10Option.setOptions(Collections.singletonMap("required", true));
        uniCredit.getConfig().setImplementerOptions(Collections.singletonMap("STYX10", styx10Option));
        IOParser ioParser = new IOParser(uniCredit);

        STYX10 styx10 = new STYX10(ioParser);

        Assertions.assertEquals(IOOrder.POST_CREATION, styx10.order());
        Assertions.assertFalse(styx10.apply(null, null, null));

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
        XS2AFactoryInput xs2AFactoryInput = new XS2AFactoryInput();
        xs2AFactoryInput.setPsu(psu);

        Assertions.assertThrows(ImplementerOptionException.class, () -> styx10.apply(xs2AFactoryInput, xs2ARequest, null));
    }
}
