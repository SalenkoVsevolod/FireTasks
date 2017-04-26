package com.example.portable.firebasetests.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.core.Notifier;
import com.example.portable.firebasetests.core.Preferences;
import com.example.portable.firebasetests.model.EntityList;
import com.example.portable.firebasetests.model.Remind;
import com.example.portable.firebasetests.model.SubTask;
import com.example.portable.firebasetests.model.Tag;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.network.FirebaseExecutorManager;
import com.example.portable.firebasetests.network.FirebaseObserver;
import com.example.portable.firebasetests.network.FirebaseUtils;
import com.example.portable.firebasetests.ui.adapters.ReminderDisplayRecyclerAdapter;
import com.example.portable.firebasetests.ui.adapters.SubtaskCheckableRecyclerAdapter;

import java.util.ArrayList;

public class TaskDisplayActivity extends BaseActivity {
    public static final String TASK_ID = "id", DAY = "day", REMINDER_TO_DELETE = "reminder";
    private TextView tagTV, nameTV, descriptionTV;
    private Task task;
    private Tag tag;
    private RecyclerView remindsRecyclerView, subtasksRecyclerView;
    private View subtasksContainer, remindsContainer;
    private CheckBox doneCheckBox;
    private ArrayList<Remind> reminds;
    private String id;
    private int day;
    private EntityList.FirebaseEntityListener<Task> taskListener;
    private EntityList.FirebaseEntityListener<Tag> tagListener;
    private EntityList.FirebaseEntityListener<Remind> remindsListener;
    private String deletingRemindId;

    public static void start(Context context, int day, String taskId) {
        Intent starter = new Intent(context, TaskDisplayActivity.class);
        starter.putExtra(TASK_ID, taskId);
        starter.putExtra(DAY, day);
        context.startActivity(starter);
    }

    //TODO refactor
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_display);
        nameTV = (TextView) findViewById(R.id.name_tv);
        descriptionTV = (TextView) findViewById(R.id.description_display);
        remindsRecyclerView = (RecyclerView) findViewById(R.id.reminder_recycler);
        subtasksRecyclerView = (RecyclerView) findViewById(R.id.subTasksRecyclerView);
        tagTV = (TextView) findViewById(R.id.tagTextView);
        doneCheckBox = (CheckBox) findViewById(R.id.task_done_checkbox);
        remindsContainer = findViewById(R.id.reminders_container);
        subtasksContainer = findViewById(R.id.subtasks_container);
        day = getIntent().getIntExtra(DAY, -1);
        id = getIntent().getStringExtra(TASK_ID);
        deletingRemindId = getIntent().getStringExtra(REMINDER_TO_DELETE);
        task = new Task();
        task.setId(id);
        tag = new Tag();
        reminds = new ArrayList<>();
        doneCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FirebaseUtils.getInstance().setTaskDone(day, id, isChecked);
            }
        });
        subtasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Toolbar toolbar = (Toolbar) findViewById(R.id.taskCreateToolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        taskListener = new EntityList.FirebaseEntityListener<Task>() {
            @Override
            public void onChanged(Task inputTask) {
                if (task.getId().equals(id)) {
                    task.init(inputTask);
                    displayTask();
                }
            }

            @Override
            public void onCreated(Task inputTask) {
                if (task.getId().equals(id)) {
                    task.init(inputTask);
                    tag = FirebaseObserver.getInstance().getTags().getById(task.getTagId());
                    displayTask();
                }
            }

            @Override
            public void onDeleted(Task task) {
                if (task.getId().equals(id)) {
                    showToast("Task deleted", true);
                    finish();
                }
            }
        };
        tagListener = new EntityList.FirebaseEntityListener<Tag>() {
            @Override
            public void onChanged(Tag inputTag) {
                if (inputTag.getId().equals(task.getTagId())) {
                    tag.init(inputTag);
                    displayTag();
                }
            }

            @Override
            public void onCreated(Tag inputTag) {
                if (inputTag.getId().equals(task.getTagId())) {
                    tag.init(inputTag);
                    displayTag();
                }
            }

            @Override
            public void onDeleted(Tag inputTag) {
                if (inputTag.getId().equals(task.getTagId())) {
                    FirebaseUtils.getInstance().deleteTask(day, id);
                }
            }
        };
        remindsListener = new EntityList.FirebaseEntityListener<Remind>() {
            @Override
            public void onChanged(Remind remind) {
                int pos = reminds.indexOf(remind);
                if (pos != -1) {
                    reminds.get(pos).init(remind);
                    remindsRecyclerView.getAdapter().notifyItemChanged(pos);
                    Notifier.removeAlarm(remind.getId());
                    Notifier.setAlarm(remind);
                }
            }

            @Override
            public void onCreated(Remind remind) {
                reminds.add(remind);
                remindsRecyclerView.getAdapter().notifyItemInserted(reminds.size());
                Notifier.removeAlarm(remind.getId());
                Notifier.setAlarm(remind);
            }

            @Override
            public void onDeleted(Remind remind) {
                int pos = reminds.indexOf(remind);
                if (pos != -1) {
                    reminds.remove(remind);
                    remindsRecyclerView.getAdapter().notifyItemRemoved(pos);
                    Notifier.removeAlarm(remind.getId());
                    Preferences.getInstance().removeRemindCode(remind.getId());
                }
            }
        };
    }

    private void displayTag() {
        tagTV.setText(tag.getName());
        tagTV.setTextColor((int) tag.getColor());
    }

    private void initReminds() {
        reminds.clear();
        for (String id : task.getReminds()) {
            reminds.add(FirebaseObserver.getInstance().getReminders().getById(id));
        }
    }

    private void displayTask() {
        if (task.getReminds() != null && task.getReminds().size() > 0) {
            remindsContainer.setVisibility(View.VISIBLE);
        } else {
            remindsContainer.setVisibility(View.GONE);
        }
        nameTV.setText(task.getName());
        if (task.getDescription().length() > 0) {
            descriptionTV.setText(task.getDescription());
            descriptionTV.setVisibility(View.VISIBLE);
        } else {
            descriptionTV.setVisibility(View.GONE);
        }
        initReminds();
        if (task.getReminds() != null && task.getReminds().size() > 0) {
            remindsRecyclerView.getAdapter().notifyDataSetChanged();
        }
        subtasksRecyclerView.getAdapter().notifyDataSetChanged();
        if (task.getSubTasks().size() != 0) {
            doneCheckBox.setVisibility(View.GONE);
            subtasksContainer.setVisibility(View.VISIBLE);
        } else {
            doneCheckBox.setVisibility(View.VISIBLE);
            doneCheckBox.setChecked(task.isDone());
            subtasksContainer.setVisibility(View.GONE);
        }
        displayTag();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Task taskInBase = FirebaseObserver.getInstance().getTasksDay(day).getById(id);
        if (taskInBase != null) {
            task.init(taskInBase);
        }
        Tag tagInBase = FirebaseObserver.getInstance().getTags().getById(task.getTagId());
        if (tagInBase != null) {
            tag.init(tagInBase);
        }

        SubtaskCheckableRecyclerAdapter adapter = new SubtaskCheckableRecyclerAdapter(task.getSubTasks(), new SubtaskCheckableRecyclerAdapter.OnSubtaskCheckListener() {
            @Override
            public void onCheck(SubTask subTask, boolean checked) {
                FirebaseUtils.getInstance().setSubTaskDone(day, id, subTask.getId(), checked);
            }
        });
        subtasksRecyclerView.setAdapter(adapter);

        remindsRecyclerView.setAdapter(new ReminderDisplayRecyclerAdapter(reminds, null));
        remindsRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        if (deletingRemindId != null) {
            FirebaseUtils.getInstance().removeReminder(day, id, deletingRemindId);
        }
        FirebaseObserver.getInstance().getTasksDay(day).subscribe(taskListener);
        FirebaseObserver.getInstance().getTags().subscribe(tagListener);
        FirebaseObserver.getInstance().getReminders().subscribe(remindsListener);
        FirebaseExecutorManager.getInstance().startDayListener(day);
        FirebaseExecutorManager.getInstance().startRemindersListener();
        FirebaseExecutorManager.getInstance().startTagsListener();
        displayTask();
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseObserver.getInstance().getTasksDay(day).unsubscribe(taskListener);
        FirebaseObserver.getInstance().getTags().unsubscribe(tagListener);
        FirebaseObserver.getInstance().getReminders().unsubscribe(remindsListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_item:
                TaskEditActivity.start(this, task);
                finish();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
