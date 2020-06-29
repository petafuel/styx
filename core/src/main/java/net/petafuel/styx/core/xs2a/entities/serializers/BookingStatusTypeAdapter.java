package net.petafuel.styx.core.xs2a.entities.serializers;

import net.petafuel.styx.core.xs2a.entities.BookingStatus;

import javax.json.bind.adapter.JsonbAdapter;

public class BookingStatusTypeAdapter implements JsonbAdapter<BookingStatus, String> {

    @Override
    public String adaptToJson(BookingStatus value) {
        return value.toString();
    }

    @Override
    public BookingStatus adaptFromJson(String s) {
        return BookingStatus.getValue(s);
    }
}
