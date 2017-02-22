package com.example.portable.firebasetests;

import java.util.Calendar;

/**
 * Created by Salenko Vsevolod on 01.02.2017.
 */

public class TimeUtils {
    public static boolean isInPast(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR), dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return weekOfYear < now.get(Calendar.WEEK_OF_YEAR) || weekOfYear <= now.get(Calendar.WEEK_OF_YEAR) && dayOfWeek != Calendar.SUNDAY && dayOfWeek < now.get(Calendar.DAY_OF_WEEK);
    }

    public static int realToAdapter(int real) {
        if (real == 1) {
            real = 6;
        } else {
            real -= 2;
        }
        return real;
    }
}