package com.example.portable.firebasetests.utils;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.core.FireTasksApp;

/**
 * Created by Salenko Vsevolod on 15.02.2017.
 */

public class StringUtils {

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
        return FireTasksApp.getInstance().getResources().getStringArray(R.array.days_of_week)[TimeUtils.realToAdapter(dayOfWeek)];
    }
}
