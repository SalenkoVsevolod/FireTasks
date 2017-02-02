package com.example.portable.firebasetests.listeners;

import com.example.portable.firebasetests.model.Task;

import java.util.ArrayList;

/**
 * Created by Portable on 25.01.2017.
 */

public interface DataChangingListener {
    void onDataChanged(ArrayList<Task> tasks);
}
