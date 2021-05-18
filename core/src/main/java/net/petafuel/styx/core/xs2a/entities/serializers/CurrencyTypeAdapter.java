package net.petafuel.styx.core.xs2a.entities.serializers;

import net.petafuel.styx.core.xs2a.entities.Currency;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.bind.adapter.JsonbAdapter;

public class CurrencyTypeAdapter implements JsonbAdapter<Currency, String> {
    private static final Logger LOG = LogManager.getLogger(CurrencyTypeAdapter.class);

    @Override
    public String adaptToJson(Currency value) {
        return value.name();
    }

    @Override
    public Currency adaptFromJson(String s) {
        try {
            return Currency.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException invalidCurrency) {
            LOG.warn("Currency='{}' is not valid", s);
            return null;
        }
    }
}
