package net.petafuel.styx.core.xs2a.entities.serializers;

import net.petafuel.styx.core.xs2a.entities.Frequency;

import javax.json.bind.adapter.JsonbAdapter;

public class FrequencyTypeAdapter implements JsonbAdapter<Frequency, String> {

    @Override
    public String adaptToJson(Frequency value) {
        return value.toString();
    }

    @Override
    public Frequency adaptFromJson(String s) {
        return Frequency.getValue(s);
    }
}
