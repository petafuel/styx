package net.petafuel.styx.core.xs2a.entities.serializers;

import net.petafuel.styx.core.xs2a.entities.PurposeCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.bind.adapter.JsonbAdapter;

public class PurposeCodeTypeAdapter implements JsonbAdapter<PurposeCode, String> {
    private static final Logger LOG = LogManager.getLogger(PurposeCodeTypeAdapter.class);

    @Override
    public String adaptToJson(PurposeCode value) {
        return value.name();
    }

    @Override
    public PurposeCode adaptFromJson(String s) {
        try {
            return PurposeCode.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException invalidCurrency) {
            LOG.warn("PurposeCode='{}' is not valid", s);
            return null;
        }
    }
}
