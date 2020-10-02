package net.petafuel.styx.core.xs2a.entities;

import net.petafuel.styx.core.xs2a.entities.serializers.BalanceTypeAdapter;

import javax.json.bind.annotation.JsonbTypeAdapter;
import java.util.Arrays;

@JsonbTypeAdapter(BalanceTypeAdapter.class)
public enum BalanceType {
    CLOSING_BOOKED("closingBooked"),
    EXPECTED("expected"),
    OPENING_BOOKED("openingBooked"),
    INTERMIN_AVAILABLE("interimAvailable"),
    INTERMIN_BOOKED("interimBooked"),
    FORWARD_AVAILABLE("forwardAvailable"),
    NON_INVOICED("nonInvoiced");

    private final String jsonValue;

    BalanceType(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    public static BalanceType getValue(String s) {
        return Arrays.asList(values()).parallelStream().filter(balanceType -> balanceType.jsonValue.equals(s)).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return jsonValue;
    }
}
