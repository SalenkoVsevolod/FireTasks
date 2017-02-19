package com.example.portable.firebasetests;

import java.util.Calendar;

/**
 * Created by Salenko Vsevolod on 01.02.2017.
 */

public class TimeUtils {
    public static boolean isDayBefore(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        if (calendar.get(Calendar.WEEK_OF_YEAR) < now.get(Calendar.WEEK_OF_YEAR)) {
            return true;
        }
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return false;
        }
        return calendar.get(Calendar.DAY_OF_WEEK) < now.get(Calendar.DAY_OF_WEEK);
    }

    public static int adapterToReal(int adapter) {
        if (adapter == 6) {
            adapter = 1;
        } else {
            adapter++;
        }
        return adapter;
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
