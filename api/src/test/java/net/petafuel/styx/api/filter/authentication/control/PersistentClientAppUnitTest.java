package net.petafuel.styx.api.filter.authentication.control;

import net.petafuel.styx.core.persistence.layers.PersistentClientApp;
import net.petafuel.styx.core.persistence.models.MasterToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersistentClientAppUnitTest {

    @Test
    void formatRestrictions() {
        MasterToken model = new MasterToken();
        String json = "{ \"pis-vimpay-instant-replenishment\": { \"max-usages\": 1 }, \"ais\": { \"max-usages\": 10 }, \"pis\": { \"max-usages\": 2 } }";
        PersistentClientApp.formatRestrictions(json, model);
        Assertions.assertNotNull(model.getRestrictions());
        Assertions.assertEquals(1, model.getRestrictions().get("pis-vimpay-instant-replenishment").getMaxUsages());
        Assertions.assertEquals(10, model.getRestrictions().get("ais").getMaxUsages());
        Assertions.assertEquals(2, model.getRestrictions().get("pis").getMaxUsages());
    }

    @Test
    void formatRestrictionsWithUnlimitedValue() {
        MasterToken model = new MasterToken();
        String json = "{ \"pis-vimpay-instant-replenishment\": { \"max-usages\": 0 }, \"ais\": { \"max-usages\": 10 }, \"pis\": { \"max-usages\": 2 } }";
        PersistentClientApp.formatRestrictions(json, model);
        Assertions.assertNotNull(model.getRestrictions());
        Assertions.assertEquals(0, model.getRestrictions().get("pis-vimpay-instant-replenishment").getMaxUsages());
        Assertions.assertEquals(10, model.getRestrictions().get("ais").getMaxUsages());
        Assertions.assertEquals(2, model.getRestrictions().get("pis").getMaxUsages());
    }

    @Test
    void formatRestrictionsWithNullValue() {
        MasterToken model = new MasterToken();
        String json = "{ \"pis-vimpay-instant-replenishment\": { \"max-usages\": 0 }, \"ais\": { \"max-usages\": null }, \"pis\": { \"max-usages\": 2 } }";
        PersistentClientApp.formatRestrictions(json, model);
        Assertions.assertNotNull(model.getRestrictions());
        Assertions.assertEquals(0, model.getRestrictions().get("pis-vimpay-instant-replenishment").getMaxUsages());
        Assertions.assertNull(model.getRestrictions().get("ais").getMaxUsages());
        Assertions.assertEquals(2, model.getRestrictions().get("pis").getMaxUsages());
    }
}