package nu.marginalia.feedlot.format;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateCleaner {
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    public static String cleanDate(ZonedDateTime date) {
        return date.format(DATE_FORMAT);
    }
}
