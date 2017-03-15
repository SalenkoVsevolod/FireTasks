package com.example.portable.firebasetests.ui.activities;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.portable.firebasetests.MySharedPreferences;
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
        task = (Task) getIntent().getSerializableExtra(TASK_ARG);


        Toolbar toolbar = (Toolbar) findViewById(R.id.taskCreateToolbar);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.titleText));
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        setFragment();
    }

    private void setFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (task.getId() == null) {
            transaction.add(R.id.task_fragment_container, TaskModifyFragment.newInstance(task));
        } else {
            transaction.add(R.id.task_fragment_container, TaskDisplayFragment.newInstance(task));
        }
        transaction.commit();
    }
/*
    @Override
    public void onBackPressed() {
        if (shouldHoldUser) {
            if (canComplete()) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}