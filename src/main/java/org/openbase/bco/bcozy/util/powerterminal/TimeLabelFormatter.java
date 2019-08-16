package org.openbase.bco.bcozy.util.powerterminal;

import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.Interval;
import org.openbase.bco.bcozy.model.LanguageSelection;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.function.BiFunction;

public class TimeLabelFormatter {

    private static final Map<Interval, BiFunction<LocalDateTime, Integer, String>> creators;

    static {
        BiFunction<LocalDateTime, Integer, String> nowLabelFunction = TimeLabelFormatter::createTimeLabelNow;
        BiFunction<LocalDateTime, Integer, String> hourLabelFunction = TimeLabelFormatter::createTimeLabelHours;
        BiFunction<LocalDateTime, Integer, String> dayLabelFunction = TimeLabelFormatter::createTimeLabelDays;
        BiFunction<LocalDateTime, Integer, String> weekLabelFunction = TimeLabelFormatter::createTimeLabelWeeks;
        BiFunction<LocalDateTime, Integer, String> monthLabelFunction = TimeLabelFormatter::createTimeLabelMonths;
        BiFunction<LocalDateTime, Integer, String> yearLabelFunction = TimeLabelFormatter::createTimeLabelYears;

        creators = Map.of(
                Interval.NOW, nowLabelFunction,
                Interval.HOURLY, hourLabelFunction,
                Interval.DAILY, dayLabelFunction,
                Interval.WEEKLY, weekLabelFunction,
                Interval.MONTHLY, monthLabelFunction,
                Interval.YEARLY, yearLabelFunction
        );
    }

    /**
     * Creates a Label describing the time for a chart
     * @param time Timestamp containing the time of the first data point
     * @param shift Number of the data point to shift the time accordingly
     * @param interval Interval size to shift the time with for each data point
     * @return Short label describing the time human readable
     */
    public static String createTimeLabel(Timestamp time, int shift, Interval interval) {
        return creators.get(interval).apply(time.toLocalDateTime(), shift);
    }

    private static String createTimeLabelNow(LocalDateTime dateTime, Integer shift) {
        return "Now";
    }

    private static String createTimeLabelHours(LocalDateTime dateTime, int shift) {
        dateTime = dateTime.plusHours(shift + 1).plusHours(dateTime.getMinute() > 29 ? 1 : 0).truncatedTo(ChronoUnit.HOURS);
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private static String createTimeLabelDays(LocalDateTime dateTime, int shift) {
        return dateTime.plusDays(shift).format(DateTimeFormatter.ofPattern("dd.MM."));
    }

    private static String createTimeLabelWeeks(LocalDateTime dateTime, int shift) {
        TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        int weeknumber = dateTime.plusWeeks(shift).get(woy);
        return "Week " + weeknumber;
    }

    private static String createTimeLabelMonths(LocalDateTime dateTime, int shift) {
        return dateTime.plusMonths(shift).getMonth().getDisplayName(TextStyle.SHORT, LanguageSelection.getSelectedLocale());
    }

    private static String createTimeLabelYears(LocalDateTime dateTime, int shift) {
        return "" + dateTime.plusYears(shift).getYear();
    }
}
