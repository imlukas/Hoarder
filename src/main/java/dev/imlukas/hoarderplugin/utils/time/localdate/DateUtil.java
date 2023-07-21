package dev.imlukas.hoarderplugin.utils.time.localdate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;
import java.util.function.BiFunction;

public class DateUtil {

    private static final Map<String, BiFunction<Long, LocalDateTime, LocalDateTime>> LOCAL_TIME_FUNCTIONS = Map.of(
            "hours", (amount, date) -> date.plusHours(amount),
            "hour", (amount, date) -> date.plusHours(amount),
            "days", (amount, date) -> date.plusDays(amount),
            "day", (amount, date) -> date.plusDays(amount),
            "weeks", (amount, date) -> date.plusWeeks(amount),
            "months", (amount, date) -> date.plusMonths(amount),
            "month", (amount, date) -> date.plusMonths(amount),
            "years", (amount, date) -> date.plusYears(amount),
            "year", (amount, date) -> date.plusYears(amount));

    public static LocalDateTime parseTime(String time) {
        LocalDateTime localDate = LocalDateTime.now();
        String[] dateSplit = time.split(" ");

        Long amount = Long.parseLong(dateSplit[0]);
        String unit = dateSplit[1].toLowerCase();

        return LOCAL_TIME_FUNCTIONS.get(unit).apply(amount, localDate);
    }

    public static Date convertToDate(LocalDateTime time) {
        return Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date convertToData(LocalDate time) {
        return Date.from(time.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static ZoneOffset getZoneOffset(LocalDateTime time) {
        return ZoneId.systemDefault().getRules().getOffset(time);
    }
}
