package net.petafuel.styx.core.xs2a.factory;

import net.petafuel.styx.core.xs2a.contracts.AISRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.BerlinGroupRequestProvider;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadAccountListRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BerlinGroupRequestFactoryTest {
    @Test
    public void testAISRequestFactory() {
        BerlinGroupRequestProvider berlinGroupRequestProvider = new BerlinGroupRequestProvider();
        AISRequestFactory requestFactory = new AISRequestFactory();
        XS2AFactoryInput factoryInput = new XS2AFactoryInput();
        AISRequest berlinGroupAccountList = requestFactory.create(berlinGroupRequestProvider.accountList(), factoryInput);
        Assertions.assertTrue(berlinGroupAccountList instanceof ReadAccountListRequest);

    }

}
