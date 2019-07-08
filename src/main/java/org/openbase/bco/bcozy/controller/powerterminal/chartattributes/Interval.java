package org.openbase.bco.bcozy.controller.powerterminal.chartattributes;

/**
 * Different intervals for in charts displayed average values.
 */
public enum Interval {
    NOW("1h"), HOURLY("1h"), DAILY("1d"), WEEKLY("1w"), MONTHLY("30d"), YEARLY("365d");

    private String influxIntervalString;

    Interval(String s) {
        this.influxIntervalString = s;
    }

    public String getInfluxIntervalString() {
        return influxIntervalString;
    }

    /**
     * Returns the fitting interval size for given time span
     * @param timeSpanDays Number of days that will be displayed in total
     * @return Interval that fits the given time span
     */
    public static Interval getDefaultIntervalForTimeSpan(int timeSpanDays) {
        if (timeSpanDays == 0) return NOW;
        if (timeSpanDays < 2) return HOURLY;
        if (timeSpanDays < 15) return DAILY;
        if (timeSpanDays < 60) return WEEKLY;
        if (timeSpanDays < 731) return MONTHLY;
        return YEARLY;
    }
}
