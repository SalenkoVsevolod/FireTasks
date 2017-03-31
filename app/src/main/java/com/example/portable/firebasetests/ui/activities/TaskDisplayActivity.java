package com.example.portable.firebasetests.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import com.example.portable.firebasetests.network.FirebaseManager;
import com.example.portable.firebasetests.network.TagSingleGetter;
import com.example.portable.firebasetests.ui.adapters.ReminderAdapter;
import com.example.portable.firebasetests.ui.adapters.SubTaskAdapter;

import java.util.Calendar;

public class TaskDisplayActivity extends AppCompatActivity {
    public static final String TASK_ARG = "task";
    private TextView name, description, tagTV;
    private CardView tagCardView;
    private Task task;
    private RecyclerView subtasksRecyclerView, remindsRecyclerView;

    public static void start(Context context, Task task) {
        Intent starter = new Intent(context, TaskDisplayActivity.class);
        starter.putExtra(TASK_ARG, task);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_display);

        name = (TextView) findViewById(R.id.name_tv);
        description = (TextView) findViewById(R.id.description_display);
        remindsRecyclerView = (RecyclerView) findViewById(R.id.reminder_recycler);
        subtasksRecyclerView = (RecyclerView) findViewById(R.id.subTasksRecyclerView);
        tagTV = (TextView) findViewById(R.id.tagTextView);
        tagCardView = (CardView) findViewById(R.id.tag_cardview);
        task = (Task) getIntent().getSerializableExtra(TASK_ARG);
        name.setText(task.getName());
        description.setText(task.getDescription());
        FirebaseManager.getInstance().setTagSingleListener(task.getTagId(), new TagSingleGetter.OnTagGetListener() {
            @Override
            public void onGet(Tag tag) {
                if (tag != null) {
                    tagTV.setText(tag.getName());
                    tagCardView.setCardBackgroundColor((int) tag.getColor());
                    tagCardView.setVisibility(View.VISIBLE);
                }
            }
        });

        findViewById(R.id.reminder_tv).setVisibility(task.getReminds().size() > 0 ? View.VISIBLE : View.GONE);
        remindsRecyclerView.setAdapter(new ReminderAdapter(task.getReminds(), null));
        remindsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        SubTaskAdapter adapter = new SubTaskAdapter(task.getSubTasks(), new SubTaskAdapter.OnSubTaskCheckBoxCliCkListener() {
            @Override
            public void onClick(SubTask subTask, boolean checked) {
                subTask.setDone(checked);
                FirebaseManager.getInstance().setSubTaskDone(task.getCalendar().get(Calendar.WEEK_OF_YEAR), task.getId(), subTask);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_item:
                TaskModifyActivity.start(this, task);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
