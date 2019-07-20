package org.openbase.bco.bcozy.controller.powerterminal.chartattributes;

public enum Interval {
    HOURLY("1h"), DAILY("1d"), WEEKLY("1w"), MONTHLY("30d"), YEARLY("365d");

    private String influxIntervalString;

    Interval(String s) {
        this.influxIntervalString = s;
    }

    public String getInfluxIntervalString() {
        return influxIntervalString;
    }

    public static Interval getDefaultIntervalForTimeSpan(int timeSpanDays) {
        if (timeSpanDays < 2) return HOURLY;
        if (timeSpanDays < 15) return DAILY;
        if (timeSpanDays < 60) return WEEKLY;
        if (timeSpanDays < 731) return MONTHLY;
        return YEARLY;
    }
}
