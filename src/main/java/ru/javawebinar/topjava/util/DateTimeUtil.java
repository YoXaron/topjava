package ru.javawebinar.topjava.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static <T extends Comparable<T>> boolean isBetweenHalfOpen(T t, T start, T end) {
        return t.compareTo(start) >= 0 && t.compareTo(end) < 0;
    }

    public static LocalDate parseDate(String date) {
        if (date.isEmpty()) {
            return null;
        }
        return LocalDate.parse(date, DATE_TIME_FORMATTER);
    }

    public static LocalTime parseTime(String time) {
        if (time.isEmpty()) {
            return null;
        }
        return LocalTime.parse(time, DATE_TIME_FORMATTER);
    }

    public static String toString(LocalDateTime ldt) {
        return ldt == null ? "" : ldt.format(DATE_TIME_FORMATTER);
    }
}
