package net.petafuel.styx.core.xs2a.entities.serializers;

import net.petafuel.styx.core.xs2a.entities.Balance;

import javax.json.bind.adapter.JsonbAdapter;

public class BalanceTypeAdapter implements JsonbAdapter<Balance.BalanceType, String> {

    @Override
    public String adaptToJson(Balance.BalanceType value) {
        return value.toString();
    }

    @Override
    public Balance.BalanceType adaptFromJson(String s) {
        return Balance.BalanceType.getValue(s);
    }
}
