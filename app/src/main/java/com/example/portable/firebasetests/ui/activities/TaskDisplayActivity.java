package com.example.portable.firebasetests.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.Remind;
import com.example.portable.firebasetests.model.SubTask;
import com.example.portable.firebasetests.model.Tag;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.network.FirebaseObserver;
import com.example.portable.firebasetests.network.FirebaseUtils;
import com.example.portable.firebasetests.ui.adapters.ReminderDisplayRecyclerAdapter;
import com.example.portable.firebasetests.ui.adapters.SubtaskCheckableRecyclerAdapter;

import java.util.ArrayList;

public class TaskDisplayActivity extends AppCompatActivity {
    public static final String TASK_ID = "id", DAY = "day";
    private TextView tagTV, nameTV, descriptionTV;
    private Task task;
    private Tag tag;
    private RecyclerView remindsRecyclerView, subtasksRecyclerView;
    private ArrayList<Remind> reminds;

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

        final int day = getIntent().getIntExtra(DAY, -1);
        final String id = getIntent().getStringExtra(TASK_ID);
        task = FirebaseObserver.getInstance().getTasksDay(day).getById(id);
        Log.i("fireSync", "input task for display:" + task.getReminds().toString());
        tag = FirebaseObserver.getInstance().getTags().getById(task.getTagId());
        reminds = new ArrayList<>();
        SubtaskCheckableRecyclerAdapter adapter = new SubtaskCheckableRecyclerAdapter(task.getSubTasks(), new SubtaskCheckableRecyclerAdapter.OnSubtaskCheckListener() {
            @Override
            public void onCheck(SubTask subTask, boolean checked) {
                subTask.setDone(checked);
                FirebaseUtils.getInstance().setSubTaskDone(day, id, subTask);
            }
        });
        subtasksRecyclerView.setAdapter(adapter);
        subtasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        View remindersContainer = findViewById(R.id.reminders_container);
        if (task.getReminds() != null && task.getReminds().size() > 0) {
            remindsRecyclerView.setAdapter(new ReminderDisplayRecyclerAdapter(reminds, null));
            remindsRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
            remindersContainer.setVisibility(View.VISIBLE);
        } else {
            remindersContainer.setVisibility(View.GONE);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.taskCreateToolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initReminds() {
        reminds.clear();
        for (String id : task.getReminds()) {
            reminds.add(FirebaseObserver.getInstance().getReminders().getById(id));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        nameTV.setText(task.getName());
        if (task.getDescription().length() > 0) {
            descriptionTV.setText(task.getDescription());
            descriptionTV.setVisibility(View.VISIBLE);
        } else {
            descriptionTV.setVisibility(View.GONE);
        }
        tagTV.setText(tag.getName());
        tagTV.setTextColor((int) tag.getColor());
        initReminds();
        if (task.getReminds() != null && task.getReminds().size() > 0) {
            remindsRecyclerView.getAdapter().notifyDataSetChanged();
        }
        subtasksRecyclerView.getAdapter().notifyDataSetChanged();
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
