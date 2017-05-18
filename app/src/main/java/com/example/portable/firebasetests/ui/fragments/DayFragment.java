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
import com.example.portable.firebasetests.core.FirebaseObserver;
import com.example.portable.firebasetests.model.EntityList;
import com.example.portable.firebasetests.model.Tag;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.network.FirebaseExecutorManager;
import com.example.portable.firebasetests.network.FirebaseUtils;
import com.example.portable.firebasetests.ui.activities.TaskDisplayActivity;
import com.example.portable.firebasetests.ui.adapters.TasksDayRecyclerAdapter;

import java.util.Collections;
import java.util.Comparator;

public class DayFragment extends Fragment {
    private static final String DAY_OF_YEAR = "dayOfYear", SORTING = "sort";
    private RecyclerView mTasksRecyclerView;
    private TextView mDeletingTextView;
    private TextView mNoTasksTextView;
    private TasksDayRecyclerAdapter mDayAdapter;

    private int mDayOfYear;
    private String mSortingTagId;


    private EntityList<Task> mTasks;
    private EntityList.FirebaseEntityListener<Task> mTasksSyncListener;
    private EntityList.FirebaseEntityListener<Tag> mTagsSyncListener;

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
            mDayOfYear = getArguments().getInt(DAY_OF_YEAR);
            mSortingTagId = getArguments().getString(SORTING);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_day, container, false);
        mTasksRecyclerView = (RecyclerView) rootView.findViewById(R.id.day_tasks_rv);
        mNoTasksTextView = (TextView) rootView.findViewById(R.id.no_tasks_tv);
        mTasks = FirebaseObserver.getInstance().getTasksDay(mDayOfYear);
        mDeletingTextView = (TextView) rootView.findViewById(R.id.deleting_tv);
        mDeletingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String taskId : mDayAdapter.getTasksForDeleting()) {
                    FirebaseUtils.getInstance().deleteTask(mDayOfYear, taskId);
                }
                hideDeleting();
            }
        });
        TasksDayRecyclerAdapter.OnTaskClickListener onTaskClickListener = new TasksDayRecyclerAdapter.OnTaskClickListener() {
            @Override
            public void onClick(Task task) {
                TaskDisplayActivity.start(getActivity(), mDayOfYear, task.getId());
            }
        };
        TasksDayRecyclerAdapter.OnDeletingListener onDeletingListener = new TasksDayRecyclerAdapter.OnDeletingListener() {
            @Override
            public void onDeletingDisplay(boolean displaying) {
                if (displaying) {
                    showDeleting();
                } else {
                    mDeletingTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onDeletingTasksNumberChanged(int number) {
                mDeletingTextView.setText(String.format(getString(R.string.delete_tasks), number));
            }
        };
        mDayAdapter = new TasksDayRecyclerAdapter(mTasks, onTaskClickListener, onDeletingListener);
        mTasksRecyclerView.setAdapter(mDayAdapter);
        mTasksSyncListener = new EntityList.FirebaseEntityListener<Task>() {
            @Override
            public void onChanged(final Task task) {
                mTasksRecyclerView.getAdapter().notifyItemChanged(mTasks.indexOf(task));
                sortTasks();
            }

            @Override
            public void onCreated(Task task) {
                mTasksRecyclerView.getAdapter().notifyItemInserted(mTasks.indexOf(task));
                setTasksVisibility(true);
                sortTasks();
            }

            @Override
            public void onDeleted(Task task) {
                mTasksRecyclerView.getAdapter().notifyItemRemoved(mTasks.indexOf(task));
                setTasksVisibility(mTasks.size() != 0);
            }
        };
        mTagsSyncListener = new EntityList.FirebaseEntityListener<Tag>() {
            @Override
            public void onChanged(Tag tag) {
                mTasksRecyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCreated(Tag tag) {
                mTasksRecyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onDeleted(Tag tag) {
                mTasksRecyclerView.getAdapter().notifyDataSetChanged();
            }
        };
        return rootView;
    }

    private void setTasksVisibility(boolean visible) {
        if (visible) {
            mNoTasksTextView.setVisibility(View.GONE);
            mTasksRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mNoTasksTextView.setVisibility(View.VISIBLE);
            mTasksRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTasksRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        FirebaseExecutorManager.getInstance().startDayListener(mDayOfYear);
        FirebaseObserver.getInstance().getTags().subscribe(mTagsSyncListener);
        mTasks.subscribe(mTasksSyncListener);
        setTasksVisibility(mTasks.size() != 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTasks.unsubscribe(mTasksSyncListener);
        FirebaseExecutorManager.getInstance().stopDayListener(mDayOfYear);
    }

    public void setSortingTagIdAndSort(String tagId) {
        if (tagId != null) {
            mSortingTagId = tagId;
            sortTasks();
        }
    }

    public void sortTasks() {
        if (mSortingTagId != null) {
            if (mTasks != null) {
                Collections.sort(mTasks, new Comparator<Task>() {
                    @Override
                    public int compare(Task o1, Task o2) {
                        if (o1.getTagId().equals(mSortingTagId)) {
                            return -1;
                        } else if (o2.getTagId().equals(mSortingTagId)) {
                            return 1;
                        }
                        return 0;
                    }
                });
                mTasksRecyclerView.getAdapter().notifyDataSetChanged();
            }
        }
    }

    public boolean hideDeleting() {
        mDeletingTextView.setVisibility(View.GONE);
        return mDayAdapter.handleBackPress();
    }

    private void showDeleting() {
        mDeletingTextView.setVisibility(View.VISIBLE);
    }
}
