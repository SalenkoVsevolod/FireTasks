package com.example.portable.firebasetests.activities;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.fragments.TaskCreateFragment;
import com.example.portable.firebasetests.model.Task;

public class TaskCreateActivity extends AppCompatActivity {
    public static final String TASK_ARG = "task";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_create);
        Task task = (Task) getIntent().getSerializableExtra(TASK_ARG);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.activity_task_create, TaskCreateFragment.newInstance(task));
        transaction.commit();
    }
}
