package com.example.portable.firebasetests.ui.activities;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.core.FireTasksApp;

/**
 * Created by Salenko Vsevolod on 26.04.2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    public static void showCancellingSnackBar(String text /*TODO some listener*/) {

    }

    public void showToast(String text, boolean lengthLong) {
        Toast toast = new Toast(FireTasksApp.getInstance());
        View view = getLayoutInflater().inflate(R.layout.toast_view, null);
        TextView textView = (TextView) view.findViewById(R.id.message);
        textView.setText(text);
        toast.setDuration(lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }

    public void showToastNotChoosed(String cause) {
        showToast(String.format(FireTasksApp.getInstance().getString(R.string.you_should_choose), cause), true);
    }

    public void configViewForClosingKeyBoard(View rootView) {
        if (!(rootView instanceof EditText)) {
            rootView.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyBoard();
                    return false;
                }
            });
        }
        if (rootView instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) rootView).getChildCount(); i++) {
                View innerView = ((ViewGroup) rootView).getChildAt(i);
                configViewForClosingKeyBoard(innerView);
            }
        }
    }

    public void hideKeyBoard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
