package com.example.portable.firebasetests.ui.activities;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.ui.fragments.TaskDisplayFragment;
import com.example.portable.firebasetests.ui.fragments.TaskModifyFragment;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;

public class TaskCreateActivity extends AppCompatActivity implements View.OnClickListener, ColorPickerDialogListener {
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
        findViewById(R.id.edit_item).setOnClickListener(this);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        task = (Task) getIntent().getSerializableExtra(TASK_ARG);
        if (savedInstanceState != null) {
            resumeFragment();
        } else if (task.getId() == null) {
            putModifyFragment(null);
        } else {
            putDisplayFragment(null);
        }
    }

    private void putDisplayFragment(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .replace(R.id.task_fragment_container, fragment == null ? TaskDisplayFragment.newInstance(task) : fragment, TaskDisplayFragment.TASK_DISPLAY_TAG)
                .commit();
        findViewById(R.id.edit_item).setVisibility(View.VISIBLE);
    }

    private void putModifyFragment(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .replace(R.id.task_fragment_container, fragment == null ? TaskModifyFragment.newInstance(task) : fragment, TaskModifyFragment.TASK_MODIFY_TAG)
                .commit();
        findViewById(R.id.edit_item).setVisibility(View.GONE);
    }

    private void resumeFragment() {
        Fragment fragment;
        if ((fragment = getFragmentManager().findFragmentByTag(TaskModifyFragment.TASK_MODIFY_TAG)) != null) {
            putModifyFragment(fragment);
        } else if ((fragment = getFragmentManager().findFragmentByTag(TaskDisplayFragment.TASK_DISPLAY_TAG)) != null) {
            putDisplayFragment(fragment);
        }
    }

    //TODO move all listeners here
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.edit_item:
                putModifyFragment(null);
                break;
        }
    }

    @Override
    public void onColorSelected(int dialogId, @ColorInt int color) {

    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }
}