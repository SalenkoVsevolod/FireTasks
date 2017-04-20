package com.example.portable.firebasetests.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.core.Notifier;
import com.example.portable.firebasetests.core.Preferences;
import com.example.portable.firebasetests.model.Remind;
import com.example.portable.firebasetests.model.SubTask;
import com.example.portable.firebasetests.model.Tag;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.network.FirebaseUtils;
import com.example.portable.firebasetests.ui.adapters.SubtaskClickableRecyclerAdapter;
import com.example.portable.firebasetests.ui.adapters.TagRecyclerAdapter;
import com.example.portable.firebasetests.utils.KeyBoardUtils;
import com.example.portable.firebasetests.utils.ToastUtils;

import java.util.ArrayList;

public class TaskEditActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TASK_ARG = "task";
    private final ArrayList<Tag> tags = new ArrayList<>();
    private EditText descriptionEdit, nameEdit;
    private RecyclerView tagsRecycler;
    private RecyclerView subTasksRecycleView, remindersRecyclerView;
    private Task task;
    private Tag lastSelectedTag;
    private TextView tagTextView, noSubtasksTextView;

    public static void start(Context context, @NonNull Task task) {
        Intent starter = new Intent(context, TaskEditActivity.class);
        starter.putExtra(TASK_ARG, task);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);
        tagTextView = (TextView) findViewById(R.id.tag_text);
        noSubtasksTextView = (TextView) findViewById(R.id.no_subtask);
        nameEdit = (EditText) findViewById(R.id.nameET);
        descriptionEdit = (EditText) findViewById(R.id.taskDescriptionEdit);
        tagsRecycler = (RecyclerView) findViewById(R.id.tags_recycler);
        subTasksRecycleView = (RecyclerView) findViewById(R.id.subTasksRecyclerView);
        remindersRecyclerView = (RecyclerView) findViewById(R.id.reminder_recycler);

        task = (Task) getIntent().getSerializableExtra(TASK_ARG);

        findViewById(R.id.add_subtask).setOnClickListener(this);
        findViewById(R.id.add_tag_imv).setOnClickListener(this);
        tagTextView.setOnClickListener(this);
        findViewById(R.id.add_tag_imv).setOnClickListener(this);
        findViewById(R.id.add_reminder_imv).setOnClickListener(this);

        KeyBoardUtils.configViewForClosingKeyBoard(this, findViewById(R.id.task_modify_root));

        if (task.getId() != null) {
            descriptionEdit.setText(task.getDescription());
            nameEdit.setText(task.getName());
        }

        initTags();
        initSubtasks();
        initReminders();

        Toolbar toolbar = (Toolbar) findViewById(R.id.task_modify_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initTags() {
        TagRecyclerAdapter.OnTagInteractionListener onTagInteractionListener = new TagRecyclerAdapter.OnTagInteractionListener() {
            @Override
            public void clickOnTag(Tag tag) {
                selectTag(tag);
            }

            @Override
            public void clickOnEdit(Tag tag) {
                TagEditorActivity.start(TaskEditActivity.this, tags, tag);
            }
        };
        tagsRecycler.setAdapter(new TagRecyclerAdapter(tags, onTagInteractionListener));
        tagsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void initReminders() {
/*TODO
        remindersRecyclerView.setAdapter(new ReminderModifyRecyclerAdapter(task.getReminds(), new ReminderModifyRecyclerAdapter.OnReminderInteractionListener() {
            @Override
            public void deleteClick(Remind remind) {
                deleteReminder(remind);
            }

            @Override
            public void reminderClick(Remind remind) {
                startActivityForResult(ReminderEditorActivity
                        .getStarterIntent(TaskEditActivity.this, remind), 36);
            }
        }));
        remindersRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        if (TimeUtils.isInPast(task.getCalendar().getTimeInMillis())) {
            findViewById(R.id.reminders_container).setVisibility(View.GONE);
        } else if (task.getReminds().size() > 0) {
            findViewById(R.id.no_reminders).setVisibility(View.GONE);
        } else {
            findViewById(R.id.reminders_container).setOnClickListener(this);
            findViewById(R.id.no_reminders).setVisibility(View.VISIBLE);
        }*/
    }

    private void initSubtasks() {
        subTasksRecycleView.setAdapter(new SubtaskClickableRecyclerAdapter(task.getSubTasks(), new SubtaskClickableRecyclerAdapter.OnSubtaskInteractionListener() {
            @Override
            public void onSubtaskClick(SubTask subTask) {
                startActivityForResult(SubtaskEditorActivity.getStarterIntent(TaskEditActivity.this, subTask), 87);
            }

            @Override
            public void onDeleteClick(SubTask subTask) {
                deleteSubtask(subTask);
            }
        }));
        subTasksRecycleView.setLayoutManager(new LinearLayoutManager(this));
        if (task.getSubTasks().size() > 0) {
            noSubtasksTextView.setVisibility(View.GONE);
        } else {
            noSubtasksTextView.setVisibility(View.VISIBLE);
            findViewById(R.id.subtasks_container).setOnClickListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /* TODO final View divider = findViewById(R.id.divider);
        FirebaseListenersManager.getInstance().setAllTagsListener(new AllTagsFirebaseListener.OnTagsSyncListener() {
            @Override
            public void onSync(ArrayList<Tag> tagsArray) {
                if (tagsArray.size() == 0) {
                    divider.setVisibility(View.GONE);
                    tagsRecycler.setVisibility(View.GONE);
                    tagTextView.setText("no tags created yet");
                    tagTextView.setTextColor(ContextCompat.getColor(TaskEditActivity.this, R.color.gray_text));
                    findViewById(R.id.tag_container).setOnClickListener(TaskEditActivity.this);
                    task.setTagId(null);
                    lastSelectedTag = null;
                } else {
                    if (tagsArray.size() > 1) {
                        tagsRecycler.setVisibility(View.VISIBLE);
                        divider.setVisibility(View.VISIBLE);
                    }
                    findViewById(R.id.tag_container).setOnClickListener(null);
                    tags.clear();
                    tags.addAll(tagsArray);
                    tagsRecycler.getAdapter().notifyDataSetChanged();
                    lastSelectedTag = null;
                    if (task.getTagId() != null) {
                        selectTag(Tag.getTagById(task.getTagId(), tagsArray));
                    } else {
                        selectTag(tags.get(0));
                    }
                }
            }
        });*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        // TODO FirebaseListenersManager.getInstance().removeAllTagsListener();
    }

    private void selectTag(Tag tag) {
        int pos = tags.indexOf(tag);
        tags.remove(pos);
        tagsRecycler.getAdapter().notifyItemRemoved(pos);
        task.setTagId(tag.getId());
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

    private boolean canComplete() {
        if (task.getName().length() == 0) {
            ToastUtils.showToastNotChoosed("name");
            return false;
        }
        if (task.getTagId() == null) {
            ToastUtils.showToastNotChoosed("tag");
            return false;
        }
        if (task.getSubTasks().size() == 0) {
            ToastUtils.showToastNotChoosed("subtasks");
            return false;
        }
        return true;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reminders_container:
            case R.id.add_reminder_imv:
                Remind remind = new Remind();
                remind.setTimeStamp(System.currentTimeMillis());
                startActivityForResult(ReminderEditorActivity.getStarterIntent(this, remind), 36);
                break;
            case R.id.tag_text:
                TagEditorActivity.start(TaskEditActivity.this, tags, lastSelectedTag);
                break;
            case R.id.subtasks_container:
            case R.id.add_subtask:
                startActivityForResult(SubtaskEditorActivity.getStarterIntent(TaskEditActivity.this, null), 87);
                break;
            case R.id.tag_container:
            case R.id.add_tag_imv:
                TagEditorActivity.start(TaskEditActivity.this, tags, null);
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
            //TODO     Notifier.removeAlarm((int) task.getReminds().get(i).getTimeStamp());
        }
        Notifier.setAlarms(task);
        FirebaseUtils.getInstance().saveTask(task);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 36:
                handleRemindResult(resultCode, data == null ? null : (Remind) data.getSerializableExtra(ReminderEditorActivity.REMINDER));
                break;
            case 87:
                handleSubtaskResult(resultCode, data == null ? null : (SubTask) data.getSerializableExtra(SubtaskEditorActivity.SUBTASK));
                break;
        }
    }

    private void handleSubtaskResult(int result, SubTask subTask) {
        switch (result) {
            case SubtaskEditorActivity.CREATE:
                task.getSubTasks().add(subTask);
                noSubtasksTextView.setVisibility(View.GONE);
                findViewById(R.id.subtasks_container).setOnClickListener(null);
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
                //TODO  task.getReminds().add(remind);
                findViewById(R.id.no_reminders).setVisibility(View.GONE);
                findViewById(R.id.reminders_container).setOnClickListener(null);
                remindersRecyclerView.getAdapter().notifyItemInserted(task.getReminds().size());
                break;
            case ReminderEditorActivity.UPDATE:
                final int index = task.getReminds().indexOf(remind);
                //TODO     task.getReminds().set(index, remind);
                remindersRecyclerView.getAdapter().notifyItemChanged(index);
                break;
        }
    }

    private void deleteSubtask(final SubTask subTask) {
        int pos = task.getSubTasks().indexOf(subTask);
        task.getSubTasks().remove(subTask);
        subTasksRecycleView.getAdapter().notifyItemRemoved(pos);
        if (task.getSubTasks().size() > 0) {
            noSubtasksTextView.setVisibility(View.GONE);
            findViewById(R.id.subtasks_container).setOnClickListener(null);
        } else {
            noSubtasksTextView.setVisibility(View.VISIBLE);
            findViewById(R.id.subtasks_container).setOnClickListener(this);
        }

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

    private void deleteReminder(final Remind remind) {
        int i = task.getReminds().indexOf(remind);
        task.getReminds().remove(i);
        remindersRecyclerView.getAdapter().notifyItemRemoved(i);
        if (task.getReminds().size() > 0) {
            findViewById(R.id.no_reminders).setVisibility(View.GONE);
            findViewById(R.id.reminders_container).setOnClickListener(null);
        } else {
            findViewById(R.id.no_reminders).setVisibility(View.VISIBLE);
            findViewById(R.id.reminders_container).setOnClickListener(this);
        }
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.task_modify_root), "Remind deleted", Snackbar.LENGTH_LONG)
                .setAction("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (remind.getTimeStamp() > System.currentTimeMillis()) {
                            //TODO         task.getReminds().add(remind);
                            findViewById(R.id.no_reminders).setVisibility(View.GONE);
                            remindersRecyclerView.getAdapter().notifyItemInserted(task.getReminds().size());
                        } else {
                            ToastUtils.showToastNotChoosed("time in future");
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
                break;
            case R.id.saveTask:
                assembleTaskAndSave();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void finishAndStartRightActivity() {
        if (task.getId() != null) {
            TaskDisplayActivity.start(this, task);
        }
        finish();
    }

    private void assembleTaskAndSave() {
        task.setName(nameEdit.getText().toString());
        task.setDescription(descriptionEdit.getText().toString());
        if (lastSelectedTag != null) {
            task.setTagId(lastSelectedTag.getId());
        } else {
            task.setTagId(null);
        }
        if (canComplete()) {
            saveTask();
            finishAndStartRightActivity();
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