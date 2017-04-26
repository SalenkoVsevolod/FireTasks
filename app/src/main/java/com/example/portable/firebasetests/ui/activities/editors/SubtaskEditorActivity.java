package com.example.portable.firebasetests.ui.activities.editors;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.SubTask;
import com.example.portable.firebasetests.ui.adapters.PrioritySpinnerAdapter;

public class SubtaskEditorActivity extends EditorActivity<SubTask> {
    public static final int REQUEST_CODE = 506;
    private static final String SUBTASK = "subtask";
    private SubTask subtask;
    private EditText editText;
    private Spinner spinner;
    private int resultCode;

    public static Intent getStarterIntent(Context context, SubTask subtask) {
        Intent starter = new Intent(context, SubtaskEditorActivity.class);
        starter.putExtra(SUBTASK, subtask);
        return starter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subtask_editor);
        setListeners();
        editText = (EditText) findViewById(R.id.subtask_name);
        subtask = (SubTask) getIntent().getSerializableExtra(SUBTASK);
        spinner = (Spinner) findViewById(R.id.priority_spinner);
        spinner.setAdapter(new PrioritySpinnerAdapter());
        if (subtask != null) {
            editText.setText(subtask.getName());
            spinner.setSelection((int) subtask.getPriority());
        }
    }

    @Override
    protected boolean assembleEntityAndProceed() {
        if (editText.getText().toString().trim().length() == 0) {
            showToastNotChoosed("subtask name");
            return false;
        }
        if (subtask == null) {
            subtask = new SubTask();
            subtask.setName(editText.getText().toString().trim());
            subtask.setId("" + System.currentTimeMillis());
            subtask.setPriority(spinner.getSelectedItemId());
            resultCode = CREATE;
        } else {
            subtask.setName(editText.getText().toString().trim());
            subtask.setPriority(spinner.getSelectedItemId());
            resultCode = UPDATE;
        }
        return true;
    }

    @Override
    protected int getResultCode() {
        return resultCode;
    }

    @Override
    protected SubTask getResultData() {
        return subtask;
    }

}
