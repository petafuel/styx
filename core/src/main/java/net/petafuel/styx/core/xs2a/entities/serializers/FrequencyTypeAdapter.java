package net.petafuel.styx.core.xs2a.entities.serializers;

import net.petafuel.styx.core.xs2a.entities.PeriodicPayment;

import javax.json.bind.adapter.JsonbAdapter;

public class FrequencyTypeAdapter implements JsonbAdapter<PeriodicPayment.Frequency, String> {

    @Override
    public String adaptToJson(PeriodicPayment.Frequency value) {
        return value.toString();
    }

    @Override
    public PeriodicPayment.Frequency adaptFromJson(String s) {
        return PeriodicPayment.Frequency.getValue(s);
    }
}
