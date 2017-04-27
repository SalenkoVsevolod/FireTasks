package com.example.portable.firebasetests.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.EntityList;
import com.example.portable.firebasetests.model.Tag;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.network.FirebaseExecutorManager;
import com.example.portable.firebasetests.network.FirebaseObserver;
import com.example.portable.firebasetests.network.FirebaseUtils;
import com.example.portable.firebasetests.ui.activities.TaskDisplayActivity;
import com.example.portable.firebasetests.ui.adapters.TasksDayRecyclerAdapter;

import java.util.Collections;
import java.util.Comparator;

public class DayFragment extends Fragment {
    private static final String DAY_OF_YEAR = "dayOfYear", SORTING = "sort";
    private RecyclerView tasksRecycler;
    private int dayOfYear;
    private String sortingTagId;
    private TextView deletingTextView;
    private TasksDayRecyclerAdapter adapter;
    private TextView noTasksTextView;
    private EntityList<Task> tasks;
    private EntityList.FirebaseEntityListener<Task> tasksListener;
    private EntityList.FirebaseEntityListener<Tag> tagListener;

    public DayFragment() {
        // Required empty public constructor
    }

    public static DayFragment newInstance(int dayOfYear, String sortingTagId) {
        DayFragment fragment = new DayFragment();
        Bundle args = new Bundle();
        args.putInt(DAY_OF_YEAR, dayOfYear);
        args.putString(SORTING, sortingTagId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dayOfYear = getArguments().getInt(DAY_OF_YEAR);
            sortingTagId = getArguments().getString(SORTING);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_day, container, false);
        tasksRecycler = (RecyclerView) rootView.findViewById(R.id.day_tasks_rv);
        noTasksTextView = (TextView) rootView.findViewById(R.id.no_tasks_tv);
        tasks = FirebaseObserver.getInstance().getTasksDay(dayOfYear);
        deletingTextView = (TextView) rootView.findViewById(R.id.deleting_tv);
        deletingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String taskId : adapter.getTasksForDeleting()) {
                    FirebaseUtils.getInstance().deleteTask(dayOfYear, taskId);
                }
                hideDeleting();
            }
        });
        TasksDayRecyclerAdapter.OnTaskClickListener onTaskClickListener = new TasksDayRecyclerAdapter.OnTaskClickListener() {
            @Override
            public void onClick(Task task) {
                TaskDisplayActivity.start(getActivity(), dayOfYear, task.getId());
            }
        };
        TasksDayRecyclerAdapter.OnDeletingListener onDeletingListener = new TasksDayRecyclerAdapter.OnDeletingListener() {
            @Override
            public void onDeletingDisplay(boolean displaying) {
                if (displaying) {
                    showDeleting();
                } else {
                    deletingTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onDeletingTasksNumberChanged(int number) {
                deletingTextView.setText(String.format(getString(R.string.delete_tasks), number));
            }
        };
        adapter = new TasksDayRecyclerAdapter(tasks, onTaskClickListener, onDeletingListener);
        tasksRecycler.setAdapter(adapter);
        tasksListener = new EntityList.FirebaseEntityListener<Task>() {
            @Override
            public void onChanged(final Task task) {
                tasksRecycler.getAdapter().notifyItemChanged(tasks.indexOf(task));
                sortTasks();
            }

            @Override
            public void onCreated(Task task) {
                tasksRecycler.getAdapter().notifyItemInserted(tasks.indexOf(task));
                setTasksVisibility(true);
                sortTasks();
            }

            @Override
            public void onDeleted(Task task) {
                tasksRecycler.getAdapter().notifyItemRemoved(tasks.indexOf(task));
                setTasksVisibility(tasks.size() != 0);
            }
        };
        tagListener = new EntityList.FirebaseEntityListener<Tag>() {
            @Override
            public void onChanged(Tag tag) {
                tasksRecycler.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCreated(Tag tag) {
                tasksRecycler.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onDeleted(Tag tag) {
                tasksRecycler.getAdapter().notifyDataSetChanged();
            }
        };
        return rootView;
    }

    private void setTasksVisibility(boolean visible) {
        if (visible) {
            noTasksTextView.setVisibility(View.GONE);
            tasksRecycler.setVisibility(View.VISIBLE);
        } else {
            noTasksTextView.setVisibility(View.VISIBLE);
            tasksRecycler.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tasksRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        FirebaseExecutorManager.getInstance().startDayListener(dayOfYear);
        FirebaseObserver.getInstance().getTags().subscribe(tagListener);
        tasks.subscribe(tasksListener);
        setTasksVisibility(tasks.size() != 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tasks.unsubscribe(tasksListener);
        FirebaseExecutorManager.getInstance().stopDayListener(dayOfYear);
    }

    public void setSortingTagIdAndSort(String tagId) {
        if (tagId != null) {
            sortingTagId = tagId;
            sortTasks();
        }
    }

    public void sortTasks() {
        if (sortingTagId != null) {
            if (tasks != null) {
                Collections.sort(tasks, new Comparator<Task>() {
                    @Override
                    public int compare(Task o1, Task o2) {
                        if (o1.getTagId().equals(sortingTagId)) {
                            return -1;
                        } else if (o2.getTagId().equals(sortingTagId)) {
                            return 1;
                        }
                        return 0;
                    }
                });
                tasksRecycler.getAdapter().notifyDataSetChanged();
            }
        }
    }

    public boolean hideDeleting() {
        deletingTextView.setVisibility(View.GONE);
        return adapter.handleBackPress();
    }

    private void showDeleting() {
        deletingTextView.setVisibility(View.VISIBLE);
    }
}
