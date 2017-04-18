package com.example.portable.firebasetests.utils;

import android.view.View;
import android.widget.Toast;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.core.FireTasksApp;

/**
 * Created by Salenko Vsevolod on 18.04.2017.
 */

public class ToastUtils {

    public static void showToast(String text, boolean lengthLong) {
        Toast toast = Toast.makeText(FireTasksApp.getInstance(), text, lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        View view = toast.getView();
        view.setPadding(16, 8, 16, 8);
        view.setBackgroundResource(R.drawable.toast_background);
        toast.setView(view);
        toast.show();
    }

    public static void showToastNotChoosed(String cause) {
        showToast(String.format(FireTasksApp.getInstance().getString(R.string.you_should_choose), cause), true);
    }

    public static void showCancellingSnackBar(String text /*TODO some listener*/) {

    }
}
