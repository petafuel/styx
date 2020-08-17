package net.petafuel.styx.api.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {
    /**
     * static methods only
     */
    private DateConverter(){}

    /**
     * Convert a Date Object into an ISO UTC formatted String
     * @param date valid Date Object
     * @return a formatted string as yyyy-MM-dd'T'HH:mm:ss.SSSZ
     */
    public static String toISOFormatUTC(Date date)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        return simpleDateFormat.format(date);
    }
}
