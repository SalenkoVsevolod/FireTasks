package com.example.portable.firebasetests.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.core.FireTasksApp;

/**
 * Created by Salenko Vsevolod on 18.04.2017.
 */

public class ToastUtils {

    public static void showToast(String text, boolean lengthLong) {
        Toast toast = new Toast(FireTasksApp.getInstance());
        View view = LayoutInflater.from(FireTasksApp.getInstance()).inflate(R.layout.toast_view, null);
        TextView textView = (TextView) view.findViewById(R.id.message);
        textView.setText(text);
        toast.setDuration(lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }

    public static void showToastNotChoosed(String cause) {
        showToast(String.format(FireTasksApp.getInstance().getString(R.string.you_should_choose), cause), true);
    }

    public static void showCancellingSnackBar(String text /*TODO some listener*/) {

    }
}
