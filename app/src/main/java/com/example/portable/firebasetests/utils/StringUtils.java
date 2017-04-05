package com.example.portable.firebasetests.utils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Salenko Vsevolod on 15.02.2017.
 */

public class StringUtils {
    private static final List<String> DAYS_OF_WEEK = Arrays.asList("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN");

    private static String formatNumber(int num) {
        return (num < 10 ? "0" : "") + num;
    }

    public static String formatDate(int... numbers) {
        StringBuilder builder = new StringBuilder();
        for (int number : numbers) {
            builder.append(formatNumber(number)).append(".");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public static String getTimeString(int hour, int minute) {
        return formatNumber(hour) + ":" + formatNumber(minute);
    }

    public static String getDayOfWeekName(int dayOfWeek) {
        return DAYS_OF_WEEK.get(TimeUtils.realToAdapter(dayOfWeek));
    }
}
