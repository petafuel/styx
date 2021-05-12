package net.petafuel.styx.core.xs2a.factory;

import net.petafuel.styx.core.xs2a.callback.entity.RealmParameter;
import net.petafuel.styx.core.xs2a.contracts.AISRequest;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.BerlinGroupRequestProvider;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.CreateConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadAccountListRequest;
import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class BerlinGroupRequestFactoryTest {
    @Test
    void testAISRequestFactory() {
        BerlinGroupRequestProvider berlinGroupRequestProvider = new BerlinGroupRequestProvider();
        AISRequestFactory requestFactory = new AISRequestFactory();
        XS2AFactoryInput factoryInput = new XS2AFactoryInput();
        AISRequest berlinGroupAccountList = requestFactory.create(berlinGroupRequestProvider.accountList(), factoryInput);
        Assertions.assertTrue(berlinGroupAccountList instanceof ReadAccountListRequest);
    }

    @Test
    void testAISConsentCreation() {
        UUID uuid = UUID.randomUUID();
        ThreadContext.put("requestUUID", uuid.toString());
        BerlinGroupRequestProvider berlinGroupRequestProvider = new BerlinGroupRequestProvider();
        AISRequestFactory requestFactory = new AISRequestFactory();
        XS2AFactoryInput factoryInput = new XS2AFactoryInput();
        Consent consent = new Consent();
        consent.setxRequestId(uuid);
        factoryInput.setConsent(consent);
        AISRequest berlinGroupAccountList = requestFactory.create(berlinGroupRequestProvider.consentCreation(), factoryInput);
        Assertions.assertTrue(berlinGroupAccountList instanceof CreateConsentRequest);

        Assertions.assertNotNull(berlinGroupAccountList.getTppRedirectUri());
        Assertions.assertTrue(berlinGroupAccountList.getTppRedirectUri().contains(uuid.toString()));
        Assertions.assertTrue(berlinGroupAccountList.getTppRedirectUri().contains(RealmParameter.OK.name().toLowerCase()));

        Assertions.assertNotNull(berlinGroupAccountList.getTppNokRedirectUri());
        Assertions.assertTrue(berlinGroupAccountList.getTppNokRedirectUri().contains(uuid.toString()));
        Assertions.assertTrue(berlinGroupAccountList.getTppNokRedirectUri().contains(RealmParameter.FAILED.name().toLowerCase()));
    }
}
