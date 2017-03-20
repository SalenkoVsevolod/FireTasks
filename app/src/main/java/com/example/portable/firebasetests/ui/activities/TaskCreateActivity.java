package com.example.portable.firebasetests.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.ui.fragments.TaskDisplayFragment;
import com.example.portable.firebasetests.ui.fragments.TaskModifyFragment;

public class TaskCreateActivity extends AppCompatActivity {
    public static final String TASK_ARG = "task";
    private Task task;

    public static void start(Context context, Task task) {
        Intent starter = new Intent(context, TaskCreateActivity.class);
        starter.putExtra(TASK_ARG, task);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_create);
        Toolbar toolbar = (Toolbar) findViewById(R.id.taskCreateToolbar);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.titleText));
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        task = (Task) getIntent().getSerializableExtra(TASK_ARG);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (task.getId() != null) {
            getFragmentManager().beginTransaction().replace(R.id.task_fragment_container, TaskDisplayFragment.newInstance(task)).commit();
            findViewById(R.id.edit_item).setVisibility(View.VISIBLE);
        } else {
            getFragmentManager().beginTransaction().replace(R.id.task_fragment_container, TaskModifyFragment.newInstance(task)).commit();
            findViewById(R.id.edit_item).setVisibility(View.GONE);
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
}