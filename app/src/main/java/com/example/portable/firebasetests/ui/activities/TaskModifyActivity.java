package com.example.portable.firebasetests.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.core.Notifier;
import com.example.portable.firebasetests.core.Preferences;
import com.example.portable.firebasetests.model.Remind;
import com.example.portable.firebasetests.model.SubTask;
import com.example.portable.firebasetests.model.Tag;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.network.FirebaseListenersManager;
import com.example.portable.firebasetests.network.FirebaseUtils;
import com.example.portable.firebasetests.network.listeners.TagsFirebaseListener;
import com.example.portable.firebasetests.network.listeners.TaskFirebaseListener;
import com.example.portable.firebasetests.ui.adapters.ReminderModifyRecyclerAdapter;
import com.example.portable.firebasetests.ui.adapters.SubtaskClickableRecyclerAdapter;
import com.example.portable.firebasetests.ui.adapters.TagRecyclerAdapter;
import com.example.portable.firebasetests.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Calendar;

public class TaskModifyActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String ID = "id", DAY = "day";
    private static final int TAG_EDITOR = 76, SUBTASK_EDITOR = 87, REMINDER_EDITOR = 36;
    private EditText descriptionEdit, nameEdit;
    private RecyclerView tagsRecycler;
    private RecyclerView subTasksRecycleView, remindersRecyclerView;
    private ArrayList<Tag> tags;
    private Tag lastSelectedTag;
    private TextView tagTextView, noSubtasksTextView, noRemindersTextView;
    private Task task;
    private View divider;

    public static void start(Context context, int day, String id) {
        Intent starter = new Intent(context, TaskModifyActivity.class);
        starter.putExtra(DAY, day);
        starter.putExtra(ID, id);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_modify);
        tagTextView = (TextView) findViewById(R.id.tag_text);
        noSubtasksTextView = (TextView) findViewById(R.id.no_subtask);
        noRemindersTextView = (TextView) findViewById(R.id.no_reminders);
        nameEdit = (EditText) findViewById(R.id.nameET);
        descriptionEdit = (EditText) findViewById(R.id.taskDescriptionEdit);
        tagsRecycler = (RecyclerView) findViewById(R.id.tags_recycler);
        divider = findViewById(R.id.divider);
        subTasksRecycleView = (RecyclerView) findViewById(R.id.subTasksRecyclerView);
        remindersRecyclerView = (RecyclerView) findViewById(R.id.reminder_recycler);

        task = new Task();
        task.setId(getIntent().getStringExtra(ID));
        task.getCalendar().set(Calendar.DAY_OF_YEAR, getIntent().getIntExtra(DAY, -1));

        tags = new ArrayList<>();

        TagRecyclerAdapter.OnTagInteractionListener onTagInteractionListener = new TagRecyclerAdapter.OnTagInteractionListener() {
            @Override
            public void clickOnTag(Tag tag) {
                selectTag(tag);
            }

            @Override
            public void clickOnEdit(Tag tag) {
                startActivityForResult(TagEditorActivity.getStarterIntent(TaskModifyActivity.this, tags, tag), TAG_EDITOR);
            }
        };

        tagTextView.setOnClickListener(this);
        findViewById(R.id.add_tag_imv).setOnClickListener(this);
        findViewById(R.id.add_reminder_imv).setOnClickListener(this);
        findViewById(R.id.add_subtask).setOnClickListener(this);
        findViewById(R.id.add_tag_imv).setOnClickListener(this);

        tagsRecycler.setAdapter(new TagRecyclerAdapter(tags, onTagInteractionListener));
        tagsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        configViewsForClosingKeyBord(findViewById(R.id.task_modify_root));

        initSubtasksRecyclerView();
        remindersRecyclerView.setAdapter(new ReminderModifyRecyclerAdapter(task.getReminds(), new ReminderModifyRecyclerAdapter.OnReminderInteractionListener() {
            @Override
            public void deleteClick(Remind remind) {
                deleteReminder(remind);
            }

            @Override
            public void reminderClick(Remind remind) {
                startActivityForResult(ReminderEditorActivity
                        .getStarterIntent(TaskModifyActivity.this, remind, task.getTimeStamp()), REMINDER_EDITOR);
            }
        }));
        remindersRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        Toolbar toolbar = (Toolbar) findViewById(R.id.task_modify_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void syncTags() {
        FirebaseListenersManager.getInstance().setTagsListener(new TagsFirebaseListener.OnTagsSyncListener() {
            @Override
            public void onSync(ArrayList<Tag> tagsArray) {
                if (tagsArray.size() == 0) {
                    divider.setVisibility(View.GONE);
                    tagsRecycler.setVisibility(View.GONE);
                    tagTextView.setText("no tags created yet");
                    tagTextView.setTextColor(ContextCompat.getColor(TaskModifyActivity.this, R.color.gray_text));
                    task.setTag(null);
                    lastSelectedTag = null;
                } else {
                    tagTextView.setVisibility(View.VISIBLE);
                    if (tagsArray.size() > 1) {
                        tagsRecycler.setVisibility(View.VISIBLE);
                        divider.setVisibility(View.VISIBLE);
                    }
                    tags.clear();
                    tags.addAll(tagsArray);
                    tagsRecycler.getAdapter().notifyDataSetChanged();
                    lastSelectedTag = null;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        syncTags();
        if (task.getId() != null) {
            syncTask();
        }
    }

    private void syncTask() {
        FirebaseListenersManager.getInstance().setTaskFirebaseListener(task.getDayOfYear(), task.getId(), new TaskFirebaseListener.OnTaskChangingListener() {
            @Override
            public void onChange(Task t) {
                task.setData(t);
                noRemindersTextView.setVisibility(task.getReminds().size() > 0 ? View.GONE : View.VISIBLE);
                remindersRecyclerView.getAdapter().notifyDataSetChanged();
                subTasksRecycleView.getAdapter().notifyDataSetChanged();
                findViewById(R.id.reminders_container).setVisibility(
                        TimeUtils.isInPast(task.getCalendar().getTimeInMillis()) ? View.GONE : View.VISIBLE);
                descriptionEdit.setText(task.getDescription());
                nameEdit.setText(task.getName());
                if (task.getTag() != null) {
                    selectTag(task.getTag());
                } else {
                    selectTag(tags.get(0));
                }
            }
        });
    }

    private void selectTag(Tag tag) {
        int pos = tags.indexOf(tag);
        tags.remove(pos);
        tagsRecycler.getAdapter().notifyItemRemoved(pos);
        task.setTag(tag);
        if (lastSelectedTag != null) {
            tags.add(lastSelectedTag);
            tagsRecycler.getAdapter().notifyItemInserted(tags.size());
        }
        drawNewTag(tag);
        lastSelectedTag = tag;
    }

    private void drawNewTag(Tag tag) {
        SpannableString content = new SpannableString(tag.getName());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tagTextView.setText(content);
        tagTextView.setTextColor((int) tag.getColor());
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseListenersManager.getInstance().removeTagsListener();
        FirebaseListenersManager.getInstance().removeTaskFirebaseListener();
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
        if (task.getTag() == null) {
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
        subTasksRecycleView.setAdapter(new SubtaskClickableRecyclerAdapter(task.getSubTasks(), new SubtaskClickableRecyclerAdapter.OnSubtaskInteractionListener() {
            @Override
            public void onSubtaskClick(SubTask subTask) {
                startActivityForResult(SubtaskEditorActivity.getStarterIntent(TaskModifyActivity.this, subTask), SUBTASK_EDITOR);
            }

            @Override
            public void onDeleteClick(SubTask subTask) {
                deleteSubtask(subTask);
            }
        }));
        subTasksRecycleView.setLayoutManager(new LinearLayoutManager(this));
        noSubtasksTextView.setVisibility(task.getSubTasks().size() > 0 ? View.GONE : View.VISIBLE);
    }

    private void deleteSubtask(final SubTask subTask) {
        int pos = task.getSubTasks().indexOf(subTask);
        task.getSubTasks().remove(subTask);
        subTasksRecycleView.getAdapter().notifyItemRemoved(pos);
        noSubtasksTextView.setVisibility(task.getSubTasks().size() > 0 ? View.GONE : View.VISIBLE);
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.task_modify_root), "Subtask deleted", Snackbar.LENGTH_LONG)
                .setAction("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        task.getSubTasks().add(subTask);
                        noSubtasksTextView.setVisibility(View.GONE);
                        subTasksRecycleView.getAdapter().notifyItemInserted(task.getSubTasks().size());
                    }
                });

        snackbar.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_reminder_imv:
                startActivityForResult(ReminderEditorActivity.getStarterIntent(this, null, task.getTimeStamp()), REMINDER_EDITOR);
                break;
            case R.id.tag_text:
                if (lastSelectedTag != null) {
                    startActivityForResult(TagEditorActivity.getStarterIntent(TaskModifyActivity.this, tags, lastSelectedTag), TAG_EDITOR);
                }
                break;
            case R.id.add_subtask:
                startActivityForResult(SubtaskEditorActivity.getStarterIntent(TaskModifyActivity.this, null), SUBTASK_EDITOR);
                break;
            case R.id.add_tag_imv:
                startActivityForResult(TagEditorActivity.getStarterIntent(TaskModifyActivity.this, tags, null), TAG_EDITOR);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.modify_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void saveTask() {
        if (task.getId() == null) {
            task.setId(Preferences.getInstance().readUserId() + "_task_" + System.currentTimeMillis());
        }
        for (int i = 0; i < task.getReminds().size(); i++) {
            Notifier.removeAlarm((int) task.getReminds().get(i).getTimeStamp());
        }
        Notifier.setAlarms(task);
        FirebaseUtils.getInstance().saveTask(task);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REMINDER_EDITOR:
                handleRemindResult(resultCode, data == null ? null : (Remind) data.getSerializableExtra(ReminderEditorActivity.REMINDER));
                break;
            case SUBTASK_EDITOR:
                handleSubtaskResult(resultCode, data == null ? null : (SubTask) data.getSerializableExtra(SubtaskEditorActivity.SUBTASK));
                break;
            case TAG_EDITOR:
                handleTagResult(resultCode, data == null ? null : (Tag) data.getSerializableExtra(TagEditorActivity.TAG));
                break;
        }
    }

    //TODO crutch
    private void handleTagResult(int result, Tag tag) {
        switch (result) {
            case TagEditorActivity.CREATE:
                FirebaseUtils.getInstance().addTag(tag);
                selectTag(tag);
                break;

            case TagEditorActivity.UPDATE:
                FirebaseUtils.getInstance().addTag(tag);
                selectTag(tag);
                break;
        }
    }

    private void handleSubtaskResult(int result, SubTask subTask) {
        switch (result) {
            case SubtaskEditorActivity.CREATE:
                task.getSubTasks().add(subTask);
                noSubtasksTextView.setVisibility(View.GONE);
                subTasksRecycleView.getAdapter().notifyItemInserted(task.getSubTasks().size());
                break;
            case SubtaskEditorActivity.UPDATE:
                int index = task.getSubTasks().indexOf(subTask);
                task.getSubTasks().set(index, subTask);
                subTasksRecycleView.getAdapter().notifyItemChanged(index);
                break;
        }
    }

    private void handleRemindResult(int result, Remind remind) {
        switch (result) {
            case ReminderEditorActivity.CREATE:
                if (remind.getTimeStamp() > System.currentTimeMillis()) {
                    task.getReminds().add(remind);
                    noRemindersTextView.setVisibility(View.GONE);
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
        }
    }


    private void deleteReminder(final Remind remind) {
        int i = task.getReminds().indexOf(remind);
        task.getReminds().remove(i);
        remindersRecyclerView.getAdapter().notifyItemRemoved(i);
        noRemindersTextView.setVisibility(task.getReminds().size() > 0 ? View.GONE : View.VISIBLE);
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.task_modify_root), "Remind deleted", Snackbar.LENGTH_LONG)
                .setAction("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (remind.getTimeStamp() > System.currentTimeMillis()) {
                            task.getReminds().add(remind);
                            noRemindersTextView.setVisibility(View.GONE);
                            remindersRecyclerView.getAdapter().notifyItemInserted(task.getReminds().size());
                        } else {
                            showErrorToast("time in future");
                        }
                    }
                });
        snackbar.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.saveTask:
                assembleTaskAndSave();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void assembleTaskAndSave() {
        task.setName(nameEdit.getText().toString());
        task.setDescription(descriptionEdit.getText().toString());
        if (lastSelectedTag != null) {
            task.setTag(lastSelectedTag);
            FirebaseUtils.getInstance().selectTag(task, task.getTag());
        } else {
            task.setTag(null);
        }
        if (canComplete()) {
            saveTask();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("All unsaved data will be removed");
        builder.setPositiveButton("quit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("cancel", null);
        builder.show();
    }
}