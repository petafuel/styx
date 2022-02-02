package net.petafuel.styx.api.ioprocessing.options;

import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.core.banklookup.sad.entities.Aspsp;
import net.petafuel.styx.core.banklookup.sad.entities.Config;
import net.petafuel.styx.core.banklookup.sad.entities.ImplementerOption;
import net.petafuel.styx.core.ioprocessing.IOParser;
import net.petafuel.styx.core.ioprocessing.IOOrder;
import net.petafuel.styx.core.ioprocessing.ImplementerOptionException;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;

class IO2UnitTest {
    private static final String TEST_BIC = "TEST7999";
    private static Aspsp aspsp;
    private static ImplementerOption io2Option;

    @BeforeAll
    static void setup() {
        io2Option = new ImplementerOption();
        io2Option.setId("IO2");
        aspsp = new Aspsp();
        aspsp.setConfig(new Config());
        aspsp.setBic(TEST_BIC);
    }

    @Test
    void testApplyJsonWithValidData() throws ImplementerOptionException {
        io2Option.setOptions(Collections.singletonMap(PaymentProduct.SEPA_CREDIT_TRANSFERS.getValue(), true));
        aspsp.getConfig().setImplementerOptions(Collections.singletonMap("IO2", io2Option));
        IOParser ioParser = new IOParser(aspsp);
        IO2 io2 = new IO2(ioParser);
        Assertions.assertEquals(IOOrder.PRE_CREATION, io2.order());

        XS2AFactoryInput xs2AFactoryInput = new XS2AFactoryInput();
        xs2AFactoryInput.setPaymentService(PaymentService.PERIODIC_PAYMENTS);
        Assertions.assertFalse(io2.apply(xs2AFactoryInput, null, null));

        xs2AFactoryInput.setPaymentService(PaymentService.PAYMENTS);
        xs2AFactoryInput.setPaymentProduct(PaymentProduct.SEPA_CREDIT_TRANSFERS);
        Assertions.assertTrue(io2.apply(xs2AFactoryInput, null, null));
        Assertions.assertEquals(PaymentProduct.SEPA_CREDIT_TRANSFERS, xs2AFactoryInput.getPaymentProduct());
    }

    @Test
    void testApplyXMLWithValidData() throws ImplementerOptionException {
        io2Option.setOptions(new HashMap<String, Boolean>());
        io2Option.getOptions().put(PaymentProduct.PAIN_001_SEPA_CREDIT_TRANSFERS.getValue(), true);
        io2Option.getOptions().put(PaymentProduct.SEPA_CREDIT_TRANSFERS.getValue(), false);

        aspsp.getConfig().setImplementerOptions(Collections.singletonMap("IO2", io2Option));
        IOParser ioParser = new IOParser(aspsp);
        IO2 io2 = new IO2(ioParser);
        Assertions.assertEquals(IOOrder.PRE_CREATION, io2.order());

        XS2AFactoryInput xs2AFactoryInput = new XS2AFactoryInput();
        xs2AFactoryInput.setPaymentService(PaymentService.PERIODIC_PAYMENTS);
        Assertions.assertFalse(io2.apply(xs2AFactoryInput, null, null));

        xs2AFactoryInput.setPaymentService(PaymentService.PAYMENTS);
        xs2AFactoryInput.setPaymentProduct(PaymentProduct.SEPA_CREDIT_TRANSFERS);
        Assertions.assertTrue(io2.apply(xs2AFactoryInput, null, null));
        Assertions.assertEquals(PaymentProduct.PAIN_001_SEPA_CREDIT_TRANSFERS, xs2AFactoryInput.getPaymentProduct());
    }

    @Test
    void testApplyUnsupportedPaymentProduct() throws ImplementerOptionException {
        io2Option.setOptions(new HashMap<String, Boolean>());
        io2Option.getOptions().put(PaymentProduct.PAIN_001_SEPA_CREDIT_TRANSFERS.getValue(), false);
        io2Option.getOptions().put(PaymentProduct.SEPA_CREDIT_TRANSFERS.getValue(), false);

        aspsp.getConfig().setImplementerOptions(Collections.singletonMap("IO2", io2Option));
        IOParser ioParser = new IOParser(aspsp);
        IO2 io2 = new IO2(ioParser);
        Assertions.assertEquals(IOOrder.PRE_CREATION, io2.order());

        XS2AFactoryInput xs2AFactoryInput = new XS2AFactoryInput();
        xs2AFactoryInput.setPaymentService(PaymentService.PERIODIC_PAYMENTS);
        Assertions.assertFalse(io2.apply(xs2AFactoryInput, null, null));

        xs2AFactoryInput.setPaymentService(PaymentService.PAYMENTS);
        xs2AFactoryInput.setPaymentProduct(PaymentProduct.SEPA_CREDIT_TRANSFERS);
        Assertions.assertThrows(StyxException.class, () -> io2.apply(xs2AFactoryInput, null, null));
    }
}