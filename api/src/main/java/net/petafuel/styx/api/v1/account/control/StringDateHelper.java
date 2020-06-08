package net.petafuel.styx.api.v1.account.control;

import net.petafuel.styx.core.xs2a.entities.XS2AJsonKeys;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringDateHelper {
    private StringDateHelper() {
    }

    public static Date fromString(String dateAsString, String format) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        simpleDateFormat.setLenient(false);
        return simpleDateFormat.parse(dateAsString);
    }

    public static Date fromString(String dateAsString) throws ParseException {
        return fromString(dateAsString, XS2AJsonKeys.DATE_FORMAT.value());
    }
}
