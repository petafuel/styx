package net.petafuel.styx.core.xs2a.entities.serializers;

import net.petafuel.styx.core.xs2a.entities.BalanceType;

import javax.json.bind.adapter.JsonbAdapter;

public class BalanceTypeAdapter implements JsonbAdapter<BalanceType, String> {

    @Override
    public String adaptToJson(BalanceType value) {
        return value.toString();
    }

    @Override
    public BalanceType adaptFromJson(String s) {
        return BalanceType.getValue(s);
    }
}
