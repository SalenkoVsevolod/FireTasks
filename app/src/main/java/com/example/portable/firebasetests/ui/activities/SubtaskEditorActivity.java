package com.example.portable.firebasetests.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.SubTask;
import com.example.portable.firebasetests.ui.adapters.PrioritySpinnerAdapter;

public class SubtaskEditorActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int UPDATE = 1, CREATE = 2;

    public static final String SUBTASK = "subtask";
    private SubTask subtask;
    private EditText editText;
    private Spinner spinner;

    public static Intent getStarterIntent(Context context, SubTask subtask) {
        Intent starter = new Intent(context, SubtaskEditorActivity.class);
        starter.putExtra(SUBTASK, subtask);
        return starter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subtask_editor);
        editText = (EditText) findViewById(R.id.subtask_name);
        subtask = (SubTask) getIntent().getSerializableExtra(SUBTASK);
        spinner = (Spinner) findViewById(R.id.priority_spinner);
        spinner.setAdapter(new PrioritySpinnerAdapter());
        if (subtask != null) {
            editText.setText(subtask.getDescription());
            spinner.setSelection((int) subtask.getPriority());
        }
        findViewById(R.id.dialog_cancel).setOnClickListener(this);
        findViewById(R.id.dialog_ok).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_ok:
                returnResult();
                break;
            case R.id.dialog_cancel:
                finish();
                break;
        }
    }

    private void returnResult() {
        if (editText.getText().toString().length() > 0) {
            Intent intent = new Intent();
            if (subtask == null) {
                subtask = new SubTask();
                subtask.setDescription(editText.getText().toString());
                subtask.setId("" + System.currentTimeMillis());
                subtask.setPriority(spinner.getSelectedItemId());
                intent.putExtra(SUBTASK, subtask);
                setResult(CREATE, intent);
                finish();
            } else {
                subtask.setDescription(editText.getText().toString());
                subtask.setPriority(spinner.getSelectedItemId());
                intent.putExtra(SUBTASK, subtask);
                setResult(UPDATE, intent);
                finish();
            }
        }
    }

    //TODO crutch
    @Override
    protected void onStop() {
        super.onStop();
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        }
    }
}
