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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.Remind;
import com.example.portable.firebasetests.model.SubTask;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.network.FirebaseListenersManager;
import com.example.portable.firebasetests.network.FirebaseUtils;
import com.example.portable.firebasetests.network.listeners.TaskFirebaseListener;
import com.example.portable.firebasetests.ui.adapters.ReminderDisplayRecyclerAdapter;
import com.example.portable.firebasetests.ui.adapters.SubtaskCheckableRecyclerAdapter;

import java.util.ArrayList;

public class TaskDisplayActivity extends AppCompatActivity {
    private static final String ID = "id", DAY = "day";
    private TextView tagTV, name, description;
    private int day;
    private String id;
    private RecyclerView remindsRecyclerView, subtasksRecyclerView;
    private View remindersContainer;
    private ArrayList<Remind> reminds;
    private ArrayList<SubTask> subTasks;

    public static Intent getStarterIntent(Context context, int dayOfYear, String id) {
        Intent starter = new Intent(context, TaskDisplayActivity.class);
        starter.putExtra(ID, id);
        starter.putExtra(DAY, dayOfYear);
        return starter;
    }

    public static void start(Context context, int dayOfYear, String taskId) {
        context.startActivity(getStarterIntent(context, dayOfYear, taskId));
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
        remindersContainer = findViewById(R.id.reminders_container);

        id = getIntent().getStringExtra(ID);
        day = getIntent().getIntExtra(DAY, -1);

        reminds = new ArrayList<>();
        subTasks = new ArrayList<>();

        remindsRecyclerView.setAdapter(new ReminderDisplayRecyclerAdapter(reminds, null));
        remindsRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));

        SubtaskCheckableRecyclerAdapter adapter = new SubtaskCheckableRecyclerAdapter(subTasks, new SubtaskCheckableRecyclerAdapter.OnSubtaskCheckListener() {
            @Override
            public void onCheck(SubTask subTask, boolean checked) {
                subTask.setDone(checked);
                FirebaseUtils.getInstance().setSubTaskDone(day, id, subTask);
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
        FirebaseListenersManager.getInstance().setTaskFirebaseListener(day, id, new TaskFirebaseListener.OnTaskChangingListener() {
            @Override
            public void onChange(Task task) {
                name.setText(task.getName());
                if (task.getDescription().length() > 0) {
                    description.setText(task.getDescription());
                    description.setVisibility(View.VISIBLE);
                } else {
                    description.setVisibility(View.GONE);
                }
                tagTV.setText(task.getTag().getName());
                tagTV.setTextColor((int) task.getTag().getColor());
                if (task.getReminds() != null && task.getReminds().size() > 0) {
                    remindersContainer.setVisibility(View.VISIBLE);
                    reminds.clear();
                    reminds.addAll(task.getReminds());
                    remindsRecyclerView.getAdapter().notifyDataSetChanged();
                } else {
                    remindersContainer.setVisibility(View.GONE);
                }
                subTasks.clear();
                subTasks.addAll(task.getSubTasks());
                subtasksRecyclerView.getAdapter().notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseListenersManager.getInstance().removeTaskFirebaseListener();
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
                TaskModifyActivity.start(this, day, id);
                finish();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}