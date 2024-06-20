package my.project.library.util.datetime;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtil {

    public static OffsetDateTime nowInUtc() {
        return OffsetDateTime.now(ZoneId.of("UTC"));
    }

    public static LocalDate convertDateToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
    }
}
