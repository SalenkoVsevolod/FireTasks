package com.example.portable.firebasetests;

import java.util.Calendar;

/**
 * Created by Salenko Vsevolod on 01.02.2017.
 */

public class TimeUtils {
    public static boolean isDayBefore(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return false;
        }
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.DAY_OF_WEEK) < now.get(Calendar.DAY_OF_WEEK);
    }

    public static boolean isOutdatedByWeek(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.WEEK_OF_YEAR) < now.get(Calendar.WEEK_OF_YEAR);
    }
}
