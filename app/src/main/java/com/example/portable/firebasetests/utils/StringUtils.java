package com.example.portable.firebasetests.utils;

import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.example.portable.firebasetests.core.FireTasksApp;

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
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public static String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = FireTasksApp.getInstance().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        if (result.contains(".")) {
            result = result.substring(0, result.lastIndexOf("."));
        }
        return result;
    }
}
