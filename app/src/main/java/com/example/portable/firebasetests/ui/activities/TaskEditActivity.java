package com.example.portable.firebasetests.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.core.Preferences;
import com.example.portable.firebasetests.model.EntityList;
import com.example.portable.firebasetests.model.Remind;
import com.example.portable.firebasetests.model.SubTask;
import com.example.portable.firebasetests.model.Tag;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.network.FirebaseObserver;
import com.example.portable.firebasetests.network.FirebaseUtils;
import com.example.portable.firebasetests.ui.adapters.ReminderModifyRecyclerAdapter;
import com.example.portable.firebasetests.ui.adapters.SubtaskClickableRecyclerAdapter;
import com.example.portable.firebasetests.ui.adapters.TagRecyclerAdapter;
import com.example.portable.firebasetests.utils.KeyBoardUtils;
import com.example.portable.firebasetests.utils.TimeUtils;
import com.example.portable.firebasetests.utils.ToastUtils;

import java.util.ArrayList;

public class TaskEditActivity extends AppCompatActivity implements View.OnClickListener, EntityList.FirebaseEntityListener<Tag> {

    private static final String TASK_ARG = "task";
    private final ArrayList<Tag> tagsInRecycler = new ArrayList<>();
    private final ArrayList<Remind> reminds = new ArrayList<>();
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

    private void setTagsRecyclerVisibility(boolean visibility) {
        if (visibility) {
            tagsRecycler.setVisibility(View.VISIBLE);
            findViewById(R.id.divider).setVisibility(View.VISIBLE);
        } else {
            tagsRecycler.setVisibility(View.GONE);
            findViewById(R.id.divider).setVisibility(View.GONE);
        }
    }

    private void initTags() {
        tagsInRecycler.addAll(FirebaseObserver.getInstance().getTags());
        TagRecyclerAdapter.OnTagInteractionListener onTagInteractionListener = new TagRecyclerAdapter.OnTagInteractionListener() {
            @Override
            public void clickOnTag(Tag tag) {
                selectTag(tag);
            }

            @Override
            public void clickOnEdit(Tag tag) {
                TagEditorActivity.start(TaskEditActivity.this, tag);
            }
        };
        tagsRecycler.setAdapter(new TagRecyclerAdapter(tagsInRecycler, onTagInteractionListener));
        tagsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        FirebaseObserver.getInstance().getTags().subscribe(this);
        if (tagsInRecycler.size() == 0) {
            setTagsRecyclerVisibility(false);
            tagTextView.setText("no tags created yet");
            tagTextView.setTextColor(ContextCompat.getColor(TaskEditActivity.this, R.color.gray_text));
            findViewById(R.id.tag_container).setOnClickListener(TaskEditActivity.this);
        } else {
            if (tagsInRecycler.size() > 1) {
                setTagsRecyclerVisibility(true);
            }
            findViewById(R.id.tag_container).setOnClickListener(null);
            tagsRecycler.getAdapter().notifyDataSetChanged();
            if (task.getTagId() != null) {
                selectTag(FirebaseObserver.getInstance().getTags().getById(task.getTagId()));
            } else {
                selectTag(tagsInRecycler.get(0));
            }
        }
    }

    private void initReminders() {
        remindersRecyclerView.setAdapter(new ReminderModifyRecyclerAdapter(reminds, new ReminderModifyRecyclerAdapter.OnReminderInteractionListener() {
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
        }
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

    private void selectTag(Tag tag) {
        int pos = tagsInRecycler.indexOf(tag);
        tagsInRecycler.remove(pos);
        tagsRecycler.getAdapter().notifyItemRemoved(pos);
        task.setTagId(tag.getId());
        if (lastSelectedTag != null) {
            tagsInRecycler.add(lastSelectedTag);
            tagsRecycler.getAdapter().notifyItemInserted(tagsInRecycler.size());
        }
        drawNewTag(tag);
        lastSelectedTag = tag;
    }

    //TODO bug with not setting listener to container on tag deleting
    private void drawNewTag(Tag tag) {
        if (tag == null) {
            tagTextView.setText("no tags created yet");
            tagTextView.setTextColor(ContextCompat.getColor(TaskEditActivity.this, R.color.gray_text));
        } else {
            SpannableString content = new SpannableString(tag.getName());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            tagTextView.setText(content);
            tagTextView.setTextColor((int) tag.getColor());
        }
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
                TagEditorActivity.start(TaskEditActivity.this, lastSelectedTag);
                break;
            case R.id.subtasks_container:
            case R.id.add_subtask:
                startActivityForResult(SubtaskEditorActivity.getStarterIntent(TaskEditActivity.this, null), 87);
                break;
            case R.id.tag_container:
            case R.id.add_tag_imv:
                TagEditorActivity.start(TaskEditActivity.this, null);
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
        FirebaseUtils.getInstance().saveReminders(reminds);
        /* TODO for (int i = 0; i < task.getReminds().size(); i++) {
                Notifier.removeAlarm((int) task.getReminds().get(i).getTimeStamp());
        }
        Notifier.setAlarms(task);*/
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
                task.getReminds().add(remind.getId());
                reminds.add(remind);
                findViewById(R.id.no_reminders).setVisibility(View.GONE);
                findViewById(R.id.reminders_container).setOnClickListener(null);
                remindersRecyclerView.getAdapter().notifyItemInserted(task.getReminds().size());
                break;
            case ReminderEditorActivity.UPDATE:
                final int index = reminds.indexOf(remind);
                reminds.set(index, remind);
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
        reminds.remove(remind);
        for (int i = 0; i < task.getReminds().size(); i++) {
            if (task.getReminds().get(i).equals(remind.getId())) {
                task.getReminds().remove(i);
                remindersRecyclerView.getAdapter().notifyItemRemoved(i);
                break;
            }
        }

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
                            task.getReminds().add(remind.getId());
                            reminds.add(remind);
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

    @Override
    public void onChanged(Tag tag) {
        if (lastSelectedTag != null && lastSelectedTag.getId().equals(tag.getId())) {
            lastSelectedTag.init(tag);
            drawNewTag(lastSelectedTag);
        } else {
            int index = tagsInRecycler.indexOf(tag);
            tagsInRecycler.set(index, tag);
            tagsRecycler.getAdapter().notifyItemChanged(index);
        }
    }

    @Override
    public void onCreated(Tag tag) {
        tagsInRecycler.add(tag);
        setTagsRecyclerVisibility(true);
        tagsRecycler.getAdapter().notifyItemInserted(tagsInRecycler.indexOf(tag));
    }

    @Override
    public void onDeleted(Tag tag) {
        if (lastSelectedTag != null && lastSelectedTag.getId().equals(tag.getId())) {
            lastSelectedTag = null;
            drawNewTag(null);
        } else {
            int index = tagsInRecycler.indexOf(tag);
            tagsInRecycler.remove(index);
            tagsRecycler.getAdapter().notifyItemRemoved(index);
        }
    }
}