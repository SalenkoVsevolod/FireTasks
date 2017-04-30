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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.core.FirebaseObserver;
import com.example.portable.firebasetests.core.Notifier;
import com.example.portable.firebasetests.core.Preferences;
import com.example.portable.firebasetests.model.EntityList;
import com.example.portable.firebasetests.model.Remind;
import com.example.portable.firebasetests.model.SubTask;
import com.example.portable.firebasetests.model.Tag;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.network.FirebaseExecutorManager;
import com.example.portable.firebasetests.network.FirebaseUtils;
import com.example.portable.firebasetests.ui.adapters.ReminderDisplayRecyclerAdapter;
import com.example.portable.firebasetests.ui.adapters.SubtaskCheckableRecyclerAdapter;

import java.util.ArrayList;

public class TaskDisplayActivity extends BaseActivity {
    public static final String TASK_ID = "id", DAY = "day", REMINDER_TO_DELETE = "reminder";
    private final ArrayList<Remind> mReminds = new ArrayList<>();
    private final Task mTask = new Task();
    private final Tag mTag = new Tag();
    private TextView mTagTextView, mNameTextView, mDescriptionTextView;
    private RecyclerView mRemindsRecyclerView, mSubtasksRecyclerView;
    private View mSubtasksContainer, mRemindsContainer, mDataContainer;
    private CheckBox mDoneCheckBox;
    private ProgressBar mProgressBar;
    private int mDay;
    private String mDeletingRemindId;

    private EntityList.FirebaseEntityListener<Task> mTasksSyncListener;
    private EntityList.FirebaseEntityListener<Tag> mTagsListener;
    private EntityList.FirebaseEntityListener<Remind> mRemindsListener;


    public static void start(Context context, int day, String taskId) {
        Intent starter = new Intent(context, TaskDisplayActivity.class);
        starter.putExtra(TASK_ID, taskId);
        starter.putExtra(DAY, day);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_display);
        mNameTextView = (TextView) findViewById(R.id.name_tv);
        mDescriptionTextView = (TextView) findViewById(R.id.description_display);
        mRemindsRecyclerView = (RecyclerView) findViewById(R.id.reminder_recycler);
        mSubtasksRecyclerView = (RecyclerView) findViewById(R.id.subTasksRecyclerView);
        mTagTextView = (TextView) findViewById(R.id.tagTextView);
        mDataContainer = findViewById(R.id.display_container);
        mDoneCheckBox = (CheckBox) findViewById(R.id.task_done_checkbox);
        mRemindsContainer = findViewById(R.id.reminders_container);
        mSubtasksContainer = findViewById(R.id.subtasks_container);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mDay = getIntent().getIntExtra(DAY, -1);
        mTask.setId(getIntent().getStringExtra(TASK_ID));
        mDeletingRemindId = getIntent().getStringExtra(REMINDER_TO_DELETE);

        mDoneCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FirebaseUtils.getInstance().setTaskDone(mDay, mTask.getId(), isChecked);
            }
        });
        mSubtasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Toolbar toolbar = (Toolbar) findViewById(R.id.taskCreateToolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        mTasksSyncListener = new EntityList.FirebaseEntityListener<Task>() {
            @Override
            public void onChanged(Task inputTask) {
                if (inputTask.getId().equals(mTask.getId())) {
                    mTask.init(inputTask);
                    displayTask();
                }
            }

            @Override
            public void onCreated(Task inputTask) {
                if (inputTask.getId().equals(mTask.getId())) {
                    mTask.init(inputTask);
                    Tag tag = FirebaseObserver.getInstance().getTags().getById(mTask.getTagId());
                    if (tag != null) {
                        mTag.init(tag);
                    }
                    setContentVisibility(true);
                    displayTask();
                }
            }

            @Override
            public void onDeleted(Task task) {
                if (task.getId().equals(mTask.getId())) {
                    showToast("Task deleted", true);
                    finish();
                }
            }
        };
        mTagsListener = new EntityList.FirebaseEntityListener<Tag>() {
            @Override
            public void onChanged(Tag inputTag) {
                if (inputTag.getId().equals(mTask.getTagId())) {
                    mTag.init(inputTag);
                    displayTag();
                }
            }

            @Override
            public void onCreated(Tag inputTag) {
                if (inputTag.getId().equals(mTask.getTagId())) {
                    mTag.init(inputTag);
                    displayTag();
                }
            }

            @Override
            public void onDeleted(Tag inputTag) {
                if (inputTag.getId().equals(mTask.getTagId())) {
                    FirebaseUtils.getInstance().deleteTask(mDay, mTask.getId());
                }
            }
        };
        mRemindsListener = new EntityList.FirebaseEntityListener<Remind>() {
            @Override
            public void onChanged(Remind remind) {
                int pos = mReminds.indexOf(remind);
                if (pos != -1) {
                    mReminds.get(pos).init(remind);
                    mRemindsRecyclerView.getAdapter().notifyItemChanged(pos);
                    Notifier.removeAlarm(remind.getId());
                    Notifier.setAlarm(remind);
                }
            }

            @Override
            public void onCreated(Remind remind) {
                mReminds.add(remind);
                mRemindsRecyclerView.getAdapter().notifyItemInserted(mReminds.size());
                Notifier.removeAlarm(remind.getId());
                Notifier.setAlarm(remind);
            }

            @Override
            public void onDeleted(Remind remind) {
                int pos = mReminds.indexOf(remind);
                if (pos != -1) {
                    mReminds.remove(remind);
                    mRemindsRecyclerView.getAdapter().notifyItemRemoved(pos);
                    Notifier.removeAlarm(remind.getId());
                    Preferences.getInstance().removeRemindCode(remind.getId());
                }
            }
        };
    }

    private void setContentVisibility(boolean visible) {
        if (visible) {
            mDataContainer.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        } else {
            mDataContainer.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void displayTag() {
        mTagTextView.setText(mTag.getName());
        mTagTextView.setTextColor((int) mTag.getColor());
    }

    private void initReminds() {
        mReminds.clear();
        for (String id : mTask.getReminds()) {
            mReminds.add(FirebaseObserver.getInstance().getReminders().getById(id));
        }
    }

    private void displayTask() {
        if (mTask.getReminds() != null && mTask.getReminds().size() > 0) {
            mRemindsContainer.setVisibility(View.VISIBLE);
        } else {
            mRemindsContainer.setVisibility(View.GONE);
        }
        mNameTextView.setText(mTask.getName());
        if (mTask.getDescription().length() > 0) {
            mDescriptionTextView.setText(mTask.getDescription());
            mDescriptionTextView.setVisibility(View.VISIBLE);
        } else {
            mDescriptionTextView.setVisibility(View.GONE);
        }
        initReminds();
        if (mTask.getReminds() != null && mTask.getReminds().size() > 0) {
            mRemindsRecyclerView.getAdapter().notifyDataSetChanged();
        }
        mSubtasksRecyclerView.getAdapter().notifyDataSetChanged();
        if (mTask.getSubTasks().size() != 0) {
            mDoneCheckBox.setVisibility(View.GONE);
            mSubtasksContainer.setVisibility(View.VISIBLE);
        } else {
            mDoneCheckBox.setVisibility(View.VISIBLE);
            mDoneCheckBox.setChecked(mTask.isDone());
            mSubtasksContainer.setVisibility(View.GONE);
        }
        displayTag();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Task taskInBase = FirebaseObserver.getInstance().getTasksDay(mDay).getById(mTask.getId());
        if (taskInBase != null) {
            mTask.init(taskInBase);
            setContentVisibility(true);
        } else {
            setContentVisibility(false);
        }
        Tag tagInBase = FirebaseObserver.getInstance().getTags().getById(mTask.getTagId());
        if (tagInBase != null) {
            mTag.init(tagInBase);
        }

        SubtaskCheckableRecyclerAdapter adapter = new SubtaskCheckableRecyclerAdapter(mTask.getSubTasks(), new SubtaskCheckableRecyclerAdapter.OnSubtaskCheckListener() {
            @Override
            public void onCheck(SubTask subTask, boolean checked) {
                FirebaseUtils.getInstance().setSubTaskDone(mDay, mTask.getId(), subTask.getId(), checked);
            }
        });
        mSubtasksRecyclerView.setAdapter(adapter);

        mRemindsRecyclerView.setAdapter(new ReminderDisplayRecyclerAdapter(mReminds, null));
        mRemindsRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        if (mDeletingRemindId != null) {
            FirebaseUtils.getInstance().removeReminder(mDay, mTask.getId(), mDeletingRemindId);
        }
        FirebaseObserver.getInstance().getTasksDay(mDay).subscribe(mTasksSyncListener);
        FirebaseObserver.getInstance().getTags().subscribe(mTagsListener);
        FirebaseObserver.getInstance().getReminders().subscribe(mRemindsListener);
        FirebaseExecutorManager.getInstance().startDayListener(mDay);
        FirebaseExecutorManager.getInstance().startRemindersListener();
        FirebaseExecutorManager.getInstance().startTagsListener();
        displayTask();
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseObserver.getInstance().getTasksDay(mDay).unsubscribe(mTasksSyncListener);
        FirebaseObserver.getInstance().getTags().unsubscribe(mTagsListener);
        FirebaseObserver.getInstance().getReminders().unsubscribe(mRemindsListener);
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
                TaskEditActivity.start(this, mTask);
                finish();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
