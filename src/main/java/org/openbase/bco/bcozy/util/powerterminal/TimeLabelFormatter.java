package org.openbase.bco.bcozy.util.powerterminal;

import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.Interval;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.function.BiFunction;

public class TimeLabelFormatter {

//    private static final Map<Interval, BiFunction<LocalDateTime, Integer, String>> creators = Map.of(
//            Interval.NOW, ((LocalDateTime dateTime, Integer shift) -> createTimeLabelNow(dateTime, shift)),
//            Interval.HOURLY, LocalDateTime::toLocalTime,
//            Interval.DAILY, LocalDateTime::toLocalDate,
//            Interval.WEEKLY, LocalDateTime::toLocalDate,
//            Interval.MONTHLY, LocalDateTime::toLocalDate,
//            Interval.YEARLY, LocalDateTime::toLocalDate
//            );

    public static String createTimeLabel(Timestamp time, int shift, Interval interval) {
        LocalTime timeLabel = time.toLocalDateTime().toLocalTime().plusHours(shift + 1);
        timeLabel = timeLabel.plusHours(timeLabel.getMinute() > 29 ? 1 : 0).truncatedTo(ChronoUnit.HOURS);
        return timeLabel.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private static String createTimeLabelNow(LocalDateTime dateTime, Integer shift) {
        return "Now";
    }

    private static String createTimeLabelHours(LocalDateTime dateTime, int shift) {
        dateTime = dateTime.plusHours(shift + 1).plusHours(dateTime.getMinute() > 29 ? 1 : 0).truncatedTo(ChronoUnit.HOURS);
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private static String createTimeLabelDays(LocalDateTime dateTime, int shift) {
        return null;
    }

    private static String createTimeLabelWeeks(LocalDateTime dateTime, int shift) {
        return null;
    }

    private static String createTimeLabelMonths(LocalDateTime dateTime, int shift) {
        return null;
    }

    private static String createTimeLabelYears(LocalDateTime dateTime, int shift) {
        return null;
    }
}
