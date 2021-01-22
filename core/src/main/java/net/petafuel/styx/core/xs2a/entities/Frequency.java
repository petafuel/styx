package net.petafuel.styx.core.xs2a.entities;

import net.petafuel.styx.core.xs2a.entities.serializers.FrequencyTypeAdapter;

import javax.json.bind.annotation.JsonbTypeAdapter;
import java.util.Arrays;

@JsonbTypeAdapter(FrequencyTypeAdapter.class)
public enum Frequency {
    YEAR("Annual"),
    SEMI("SemiAnnual"),
    QUTR("Quarterly"),
    TOMN("EveryTwoMonths"),
    MNTH("Monthly"),
    TWMW("TwiceAMonth"),
    TOWK("EveryTwoWeeks"),
    WEEK("Weekly"),
    DAIL("Daily"),
    ADHO("Adhoc"),
    INDA("IntraDay"),
    OVNG("Overnight"),
    ONDE("OnDemand");

    private final String value;

    Frequency(String fullName) {
        this.value = fullName;
    }

    public static Frequency getValue(String s) {
        return Arrays.asList(values())
                .parallelStream()
                .filter(bookingStatus -> bookingStatus.value.equals(s) || bookingStatus.name().equals((s)))
                .findFirst()
                .orElse(null);
    }

    public String getValue() {
        return value;
    }

}
