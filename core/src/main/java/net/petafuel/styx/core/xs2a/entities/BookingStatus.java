package net.petafuel.styx.core.xs2a.entities;

import net.petafuel.styx.core.xs2a.entities.serializers.BookingStatusTypeAdapter;

import javax.json.bind.annotation.JsonbTypeAdapter;
import java.util.Arrays;

@JsonbTypeAdapter(BookingStatusTypeAdapter.class)
public enum BookingStatus {
    BOOKED("booked"),
    PENDING("pending"),
    INFORMATION("information"),
    BOTH("both");

    private final String jsonValue;

    BookingStatus(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    public static BookingStatus getValue(String s) {
        return Arrays.asList(values()).parallelStream().filter(bookingStatus -> bookingStatus.jsonValue.equals(s)).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return jsonValue;
    }
}
