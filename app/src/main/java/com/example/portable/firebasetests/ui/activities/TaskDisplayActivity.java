package com.example.portable.firebasetests.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.SubTask;
import com.example.portable.firebasetests.model.Tag;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.network.FirebaseListenersManager;
import com.example.portable.firebasetests.network.FirebaseUtils;
import com.example.portable.firebasetests.network.listeners.TagFirebaseListener;
import com.example.portable.firebasetests.ui.adapters.ReminderDisplayRecyclerAdapter;
import com.example.portable.firebasetests.ui.adapters.SubtaskCheckableRecyclerAdapter;

import java.util.Calendar;

public class TaskDisplayActivity extends AppCompatActivity {
    public static final String TASK_ARG = "task";
    private TextView tagTV;
    private Task task;

    public static void start(Context context, @NonNull Task task) {
        Intent starter = new Intent(context, TaskDisplayActivity.class);
        starter.putExtra(TASK_ARG, task);
        context.startActivity(starter);
    }

    //TODO refactor
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_display);
        TextView name = (TextView) findViewById(R.id.name_tv);
        TextView description = (TextView) findViewById(R.id.description_display);
        RecyclerView remindsRecyclerView = (RecyclerView) findViewById(R.id.reminder_recycler);
        RecyclerView subtasksRecyclerView = (RecyclerView) findViewById(R.id.subTasksRecyclerView);
        tagTV = (TextView) findViewById(R.id.tagTextView);
        task = (Task) getIntent().getSerializableExtra(TASK_ARG);
        name.setText(task.getName());
        if (task.getDescription().length() > 0) {
            description.setText(task.getDescription());
            description.setVisibility(View.VISIBLE);
        } else {
            description.setVisibility(View.GONE);
        }

        View remindersContainer = findViewById(R.id.reminders_container);
        if (task.getReminds() != null && task.getReminds().size() > 0) {
            remindsRecyclerView.setAdapter(new ReminderDisplayRecyclerAdapter(task.getReminds(), null));
            remindsRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
            remindersContainer.setVisibility(View.VISIBLE);
        } else {
            remindersContainer.setVisibility(View.GONE);
        }
        SubtaskCheckableRecyclerAdapter adapter = new SubtaskCheckableRecyclerAdapter(task.getSubTasks(), new SubtaskCheckableRecyclerAdapter.OnSubtaskCheckListener() {
            @Override
            public void onCheck(SubTask subTask, boolean checked) {
                subTask.setDone(checked);
                FirebaseUtils.getInstance().setSubTaskDone(task.getCalendar().get(Calendar.DAY_OF_YEAR), task.getId(), subTask);
            }
        });
        subtasksRecyclerView.setAdapter(adapter);
        subtasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Toolbar toolbar = (Toolbar) findViewById(R.id.taskCreateToolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseListenersManager.getInstance().setTagListener(task.getTagId(), new TagFirebaseListener.OnTagGetListener() {
            @Override
            public void onGet(Tag tag) {
                tagTV.setText(tag.getName());
                tagTV.setTextColor((int) tag.getColor());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseListenersManager.getInstance().removeTagListener();
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
