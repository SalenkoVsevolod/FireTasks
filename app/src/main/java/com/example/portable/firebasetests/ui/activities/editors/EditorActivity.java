package com.example.portable.firebasetests.ui.activities.editors;

import android.content.Intent;
import android.view.View;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.FirebaseEntity;
import com.example.portable.firebasetests.ui.activities.BaseActivity;

/**
 * Created by Salenko Vsevolod on 26.04.2017.
 */

public abstract class EditorActivity<T extends FirebaseEntity> extends BaseActivity {
    public static final int CREATE = 1, UPDATE = 2;

    protected void setListeners() {
        findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.dialog_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okClick();
            }
        });
    }

    protected abstract boolean assembleEntityAndProceed();

    protected abstract int getResultCode();

    protected abstract T getResultData();

    private void okClick() {
        if (assembleEntityAndProceed()) {
            returnEntity(getResultCode(), getResultData());
        }
    }

    private void returnEntity(int code, T t) {
        Intent intent = new Intent();
        intent.putExtra(getClass().getSimpleName(), t);
        setResult(code, intent);
        finish();
    }
}
