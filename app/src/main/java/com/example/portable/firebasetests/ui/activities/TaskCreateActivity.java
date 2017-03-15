package com.example.portable.firebasetests.ui.activities;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.portable.firebasetests.MySharedPreferences;
import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.TagsColors;
import com.example.portable.firebasetests.ui.adapters.SubTaskAdapter;
import com.example.portable.firebasetests.ui.adapters.TagAdapter;
import com.example.portable.firebasetests.ui.OnListItemClickListener;
import com.example.portable.firebasetests.model.SubTask;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.notifications.Notifier;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import static android.R.string.cancel;
import static android.R.string.ok;

public class TaskCreateActivity extends AppCompatActivity {
    public static final String TASK_ARG = "task";
    private Task task;
    private EditText descriptionEdit;
    private Button okButton, addSubTaskButton;
    private TextView timeTextView;
    private Spinner tagSpinner;
    private RecyclerView subTasksRecycleView;
    private String userId;
    private boolean shouldHoldUser = false;

    public static void start(Context context, Task task) {
        Intent starter = new Intent(context, TaskCreateActivity.class);
        starter.putExtra(TASK_ARG, task);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_create);
        descriptionEdit = (EditText) findViewById(R.id.taskDescriptionEdit);
        tagSpinner = (Spinner) findViewById(R.id.tagSpinner);
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        okButton = (Button) findViewById(R.id.addTaskButton);
        subTasksRecycleView = (RecyclerView) findViewById(R.id.subTasksRecyclerView);
        addSubTaskButton = (Button) findViewById(R.id.addSubTaskButton);
        task = (Task) getIntent().getSerializableExtra(TASK_ARG);
        userId = MySharedPreferences.readUserId(this);
        initInterface();
        Toolbar toolbar = (Toolbar) findViewById(R.id.taskCreateToolbar);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.titleText));
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        configViewsForClosingKeyBord(descriptionEdit.getRootView());
    }

    private void configViewsForClosingKeyBord(View rootView) {
        if (!(rootView instanceof EditText)) {
            rootView.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard();
                    return false;
                }
            });
        }
        if (rootView instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) rootView).getChildCount(); i++) {
                View innerView = ((ViewGroup) rootView).getChildAt(i);
                configViewsForClosingKeyBord(innerView);
            }
        }
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void initInterface() {
        if (task.getId() == null) {
            task.setId(userId + "_task_" + System.currentTimeMillis());
        } else {
            shouldHoldUser = true;
            descriptionEdit.setText(task.getDescription());
            if (task.isTimeSpecified()) {
                timeTextView.setText(task.getTimeString());
            }
        }
        tagSpinner.setAdapter(new TagAdapter(this, TagsColors.getTags()));
        initSubtasksRecyclerView();
        tagSpinner.setSelection((int) task.getTagIndex());
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.setDescription(descriptionEdit.getText().toString());
                if (canComplete()) {
                    saveTask();
                    onBackPressed();
                }
            }
        });
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });
        addSubTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddSubTaskDialog();
            }
        });
    }

    private void initSubtasksRecyclerView() {
        final SubTaskAdapter subTaskAdapter = new SubTaskAdapter(task.getSubTasks());
        subTaskAdapter.setLongClickListener(new OnListItemClickListener() {
            @Override
            public void onLongClick(int index) {
                openSubtaskDeleteDialog(subTaskAdapter.getItem(index));
            }
        });
        subTasksRecycleView.setAdapter(subTaskAdapter);
        subTasksRecycleView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.setTimeInMillis(task.getTimeStamp());
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                task.setTimeStamp(calendar.getTimeInMillis());
                timeTextView.setText(task.getTimeString());
                task.setTimeSpecified();
            }
        };
        calendar.setTimeInMillis(task.getTimeStamp());
        new TimePickerDialog(this, listener,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void saveTask() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(userId).child("" + task.getCalendar().get(Calendar.WEEK_OF_YEAR));
        task.setTagIndex(tagSpinner.getSelectedItemPosition());
        if (task.isTimeSpecified() && task.getTimeStamp() > System.currentTimeMillis()) {
            Notifier.removeAlarm(this, (int) task.getTimeStamp());
            Notifier.setAlarm(task, this);
        }
        myRef.child(task.getId()).setValue(task);
        for (SubTask subTask : task.getSubTasks()) {
            myRef = database.getReference("users").child(userId).child("" + task.getCalendar().get(Calendar.WEEK_OF_YEAR)).child(task.getId()).child("subTasks")
                    .child(subTask.getId());
            myRef.setValue(subTask);
        }
    }

    private boolean canComplete() {
        if (task.getDescription().length() == 0) {
            showErrorToast("description");
            return false;
        }
        if (task.getSubTasks().size() == 0) {
            showErrorToast("subtasks");
            return false;
        }
        return true;

    }

    private void showErrorToast(String cause) {
        Toast toast = Toast.makeText(this, "You should choose " + cause, Toast.LENGTH_LONG);
        toast.getView().setBackgroundColor(Color.RED);
        toast.show();
    }

    private void openAddSubTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        builder.setView(editText);
        builder.setMessage("Subtask's description:");
        builder.setPositiveButton(ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editText.getText().toString().length() > 0) {
                    SubTask subtask = new SubTask(editText.getText().toString());
                    subtask.setId(task.getId() + "_subtask_" + System.currentTimeMillis());
                    task.getSubTasks().add(subtask);
                    subTasksRecycleView.getAdapter().notifyDataSetChanged();
                }
            }
        });
        builder.setNegativeButton(cancel, null);
        builder.setCancelable(true);
        builder.show();
    }

    private void openSubtaskDeleteDialog(final SubTask subTask) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete \"" + subTask.getDescription() + "\" subtask?");
        builder.setPositiveButton(ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("users").child(userId).child("" + task.getCalendar().get(Calendar.WEEK_OF_YEAR)).child(task.getId()).child("subTasks").child(subTask.getId());
                myRef.setValue(null);
                task.getSubTasks().remove(subTask);
                if (task.getSubTasks().size() == 0) {
                    shouldHoldUser = true;
                }
                subTasksRecycleView.getAdapter().notifyDataSetChanged();
            }
        });
        builder.setCancelable(true);
        builder.setNegativeButton(cancel, null);
        builder.show();
    }

    @Override
    public void onBackPressed() {
        if (shouldHoldUser) {
            if (canComplete()) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}