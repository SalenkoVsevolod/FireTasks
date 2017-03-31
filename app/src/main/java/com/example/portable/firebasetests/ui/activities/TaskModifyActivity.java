package com.example.portable.firebasetests.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.core.Notifier;
import com.example.portable.firebasetests.core.Preferences;
import com.example.portable.firebasetests.model.Remind;
import com.example.portable.firebasetests.model.SubTask;
import com.example.portable.firebasetests.model.Tag;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.network.FirebaseManager;
import com.example.portable.firebasetests.network.TagsObserverTask;
import com.example.portable.firebasetests.ui.adapters.ReminderAdapter;
import com.example.portable.firebasetests.ui.adapters.SubTaskAdapter;
import com.example.portable.firebasetests.ui.adapters.TagAdapter;

import java.util.ArrayList;

import static com.example.portable.firebasetests.model.SubTask.PRIORITIES;

public class TaskModifyActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TASK_ARG = "task";
    private EditText descriptionEdit, nameEdit;
    private Button okButton, addSubTaskButton;
    private Spinner tagSpinner;
    private RecyclerView subTasksRecycleView, remindersRecyclerView;
    private Task task;
    private ImageView editTag;
    private ArrayList<Tag> tags;

    public static void start(Context context, Task task) {
        Intent starter = new Intent(context, TaskModifyActivity.class);
        starter.putExtra(TASK_ARG, task);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_modify);
        task = (Task) getIntent().getSerializableExtra(TASK_ARG);
        nameEdit = (EditText) findViewById(R.id.nameET);
        descriptionEdit = (EditText) findViewById(R.id.taskDescriptionEdit);
        tagSpinner = (Spinner) findViewById(R.id.tagSpinner);
        okButton = (Button) findViewById(R.id.addTaskButton);
        subTasksRecycleView = (RecyclerView) findViewById(R.id.subTasksRecyclerView);
        addSubTaskButton = (Button) findViewById(R.id.addSubTaskButton);
        remindersRecyclerView = (RecyclerView) findViewById(R.id.reminder_recycler);
        editTag = (ImageView) findViewById(R.id.edit_tag_imv);
        editTag.setOnClickListener(this);
        findViewById(R.id.add_tag_imv).setOnClickListener(this);
        findViewById(R.id.add_reminder_imv).setOnClickListener(this);
        configViewsForClosingKeyBord(findViewById(R.id.task_modify_root));
        if (task.getId() != null) {
            descriptionEdit.setText(task.getDescription());
            nameEdit.setText(task.getName());
        }

        initSubtasksRecyclerView();
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.setName(nameEdit.getText().toString());
                task.setDescription(descriptionEdit.getText().toString());
                if (tagSpinner.getVisibility() == View.VISIBLE) {
                    task.setTagId(((Tag) tagSpinner.getSelectedItem()).getId());
                } else {
                    task.setTagId(null);
                }

                if (canComplete()) {
                    saveTask();
                    finish();
                }
            }
        });
        addSubTaskButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                editSubtask(null);
            }
        });
        remindersRecyclerView.setAdapter(new ReminderAdapter(task.getReminds(), new ReminderAdapter.OnRemindClickListener() {
            @Override
            public void onClick(Remind remind) {
                startActivityForResult(ReminderEditorActivity
                        .getStarterIntent(TaskModifyActivity.this, remind, task.getTimeStamp()), 36);
            }
        }));
        remindersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Toolbar toolbar = (Toolbar) findViewById(R.id.task_modify_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("tagsedit", "task modify start");
        FirebaseManager.getInstance().setTagsListener(new TagsObserverTask.OnTagsSyncListener() {
            @Override
            public void onSync(ArrayList<Tag> tags) {
                if (tags.size() == 0) {
                    Log.i("tagsedit", "sync, set invisible");
                    tagSpinner.setVisibility(View.GONE);
                    editTag.setVisibility(View.GONE);
                    task.setTagId(null);
                } else {
                    Log.i("tagsedit", "sync, set visible");
                    tagSpinner.setVisibility(View.VISIBLE);
                    editTag.setVisibility(View.VISIBLE);
                    TaskModifyActivity.this.tags = tags;
                    tagSpinner.setAdapter(new TagAdapter(TaskModifyActivity.this, tags));
                    if (task.getTagId() != null) {
                        int pos = -1;
                        for (int i = 0; i < tags.size(); i++) {
                            if (tags.get(i).getId().equals(task.getTagId())) {
                                pos = i;
                                break;
                            }
                        }
                        tagSpinner.setSelection(pos);
                    } else {
                        tagSpinner.setSelection(0);
                    }

                }

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("tagsedit", "modify stop");
        FirebaseManager.getInstance().removeTagsListener();
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

    private void showErrorToast(String cause) {
        Toast toast = Toast.makeText(this, "You should choose " + cause, Toast.LENGTH_LONG);
        toast.getView().setBackgroundColor(Color.RED);
        toast.show();
    }

    private boolean canComplete() {
        if (task.getName().length() == 0) {
            showErrorToast("name");
            return false;
        }
        if (task.getDescription().length() == 0) {
            showErrorToast("description");
            return false;
        }
        if (task.getTagId() == null) {
            showErrorToast("tag");
            return false;
        }
        if (task.getSubTasks().size() == 0) {
            showErrorToast("subtasks");
            return false;
        }
        return true;

    }

    private void initSubtasksRecyclerView() {
        SubTaskAdapter subTaskAdapter = new SubTaskAdapter(task.getSubTasks(), new SubTaskAdapter.OnSubTaskClickListener() {
            @Override
            public void onClick(SubTask subTask) {
                editSubtask(subTask);
            }
        });
        subTasksRecycleView.setAdapter(subTaskAdapter);
        subTasksRecycleView.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_reminder_imv:
                startActivityForResult(ReminderEditorActivity.getStarterIntent(this, null, task.getTimeStamp()), 36);
                break;
            case R.id.add_tag_imv:
                TagEditorActivity.start(this, tags, null);
                break;
            case R.id.edit_tag_imv:
                TagEditorActivity.start(this, tags, (Tag) tagSpinner.getSelectedItem());
                break;
        }
    }


    private void editSubtask(final SubTask subTask) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.subtask_editor, null);
        final EditText editText = (EditText) view.findViewById(R.id.subtask_name);
        final Spinner spinner = (Spinner) view.findViewById(R.id.priority_spinner);
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, PRIORITIES));
        Button button = (Button) view.findViewById(R.id.delete_subtask);
        if (subTask != null) {
            button.setVisibility(View.VISIBLE);
            editText.setText(subTask.getDescription());
            spinner.setSelection((int) subTask.getPriority() - 1);
        }
        builder.setView(view);
        builder.setCancelable(true);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editText.getText().toString().length() > 0) {
                    if (subTask == null) {
                        SubTask s = new SubTask();
                        s.setDescription(editText.getText().toString());
                        s.setId("" + System.currentTimeMillis());
                        s.setPriority(spinner.getSelectedItemId() + 1);
                        task.getSubTasks().add(s);
                        subTasksRecycleView.getAdapter().notifyItemInserted(task.getSubTasks().indexOf(s));
                    } else {
                        int index = task.getSubTasks().indexOf(subTask);
                        subTask.setDescription(editText.getText().toString());
                        subTask.setPriority(spinner.getSelectedItemId() + 1);
                        task.getSubTasks().set(index, subTask);
                        subTasksRecycleView.getAdapter().notifyItemChanged(index);
                    }
                }
            }
        });
        final AlertDialog dialog = builder.show();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = task.getSubTasks().indexOf(subTask);
                task.getSubTasks().remove(subTask);
                subTasksRecycleView.getAdapter().notifyItemRemoved(pos);
                dialog.dismiss();
            }
        });

    }

    private void saveTask() {
        if (task.getId() == null) {
            task.setId(Preferences.getInstance().readUserId() + "_task_" + System.currentTimeMillis());
        }
        for (Remind r : task.getReminds()) {
            Log.i("remind", r.getId() + ":" + r);
        }
        for (int i = 0; i < task.getReminds().size(); i++) {
            Notifier.removeAlarm((int) task.getReminds().get(i).getTimeStamp());
        }
        Notifier.setAlarms(task);
        FirebaseManager.getInstance().saveTask(task);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 36:
                handleRemindResult(resultCode, data == null ? null : (Remind) data.getSerializableExtra(ReminderEditorActivity.REMINDER));
                break;
        }
    }

    private void handleRemindResult(int result, Remind remind) {
        switch (result) {
            case ReminderEditorActivity.CANCEL:
                //TODO
                break;
            case ReminderEditorActivity.CREATE:
                if (remind.getTimeStamp() > System.currentTimeMillis()) {
                    task.getReminds().add(remind);
                    remindersRecyclerView.getAdapter().notifyItemInserted(task.getReminds().size());
                } else {
                    showErrorToast("time in future");
                }
                break;
            case ReminderEditorActivity.UPDATE:
                if (remind.getTimeStamp() > System.currentTimeMillis()) {
                    final int index = task.getReminds().indexOf(remind);
                    task.getReminds().set(index, remind);
                    remindersRecyclerView.getAdapter().notifyItemChanged(index);
                } else {
                    showErrorToast("time in future");
                }
                break;
            case ReminderEditorActivity.DELETE:
                int i = task.getReminds().indexOf(remind);
                task.getReminds().remove(i);
                remindersRecyclerView.getAdapter().notifyItemRemoved(i);
                Notifier.removeAlarm((int) remind.getTimeStamp());
                break;
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

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onContextItemSelected(item);
    }
}