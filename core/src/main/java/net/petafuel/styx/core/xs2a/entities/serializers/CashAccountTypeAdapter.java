package net.petafuel.styx.core.xs2a.entities.serializers;

import net.petafuel.styx.core.xs2a.entities.CashAccountType;

import javax.json.bind.adapter.JsonbAdapter;

public class CashAccountTypeAdapter implements JsonbAdapter<CashAccountType, String> {

    @Override
    public String adaptToJson(CashAccountType value) {
        return value.name();
    }

    @Override
    public CashAccountType adaptFromJson(String s) {
        return CashAccountType.valueOf(s.toUpperCase());
    }
}