package com.example.portable.firebasetests;

/**
 * Created by Salenko Vsevolod on 15.02.2017.
 */

public class StringUtils {
    public static String formatNumber(int num) {
        return (num < 10 ? "0" : "") + num;
    }

    public static String formatDate(int... numbers) {
        StringBuilder builder = new StringBuilder();
        for (int number : numbers) {
            builder.append(formatNumber(number)).append(".");
        }
        builder.deleteCharAt(builder.length()-1);
        return builder.toString();
    }
}
