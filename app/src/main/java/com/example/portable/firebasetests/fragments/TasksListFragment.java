package com.example.portable.firebasetests.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.portable.firebasetests.Notifier;
import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.adapters.TasksAdapter;
import com.example.portable.firebasetests.listeners.DataChangingListener;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.tasks.DataObserverTask;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.R.string.cancel;
import static android.R.string.ok;

public class TasksListFragment extends Fragment {
    private static final String ID_ARG = "id_arg";
    private String id;
    private ProgressBar progressBar;
    private DataObserverTask dataObserverTask;
    private FloatingActionButton addButton;
    private TextView emptyTextView;
    private ExpandableListView expandableListView;

    public TasksListFragment() {
    }

    public static TasksListFragment newInstance(String id) {
        TasksListFragment fragment = new TasksListFragment();
        Bundle args = new Bundle();
        args.putString(ID_ARG, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            id = getArguments().getString(ID_ARG);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tasks_list, container, false);
        addButton = (FloatingActionButton) rootView.findViewById(R.id.addFloatingButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addClick();
            }
        });
        progressBar = (ProgressBar) rootView.findViewById(R.id.tasksListProgressBar);
        emptyTextView = (TextView) rootView.findViewById(R.id.emptyTasksTextView);
        expandableListView = (ExpandableListView) rootView.findViewById(R.id.tasksExpandableListView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataObserverTask = new DataObserverTask(getActivity(), id);
        dataObserverTask.setDataChangingListener(new DataChangingListener() {
            @Override
            public void onDataChanged(ArrayList<Task> tasks) {
                if (tasks == null) {
                    tasks = new ArrayList<>();
                }
                setVisibilities(tasks.isEmpty());
                final TasksAdapter adapter = new TasksAdapter(getActivity(), tasks);
                expandableListView.setAdapter(adapter);
                expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                        onTaskClick((Task) adapter.getChild(groupPosition, childPosition));
                        return false;
                    }
                });
                expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                            int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                            int childPosition = ExpandableListView.getPackedPositionChild(id);
                            deleteDialog((Task) adapter.getChild(groupPosition, childPosition));
                            return true;
                        }
                        return false;
                    }
                });

            }
        });
        dataObserverTask.execute();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dataObserverTask.cancel(true);
    }

    private void onTaskClick(Task task) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.activity_tasks, TaskCreateFragment.newInstance(TasksListFragment.this.id, task));
        transaction.addToBackStack(null);
        transaction.remove(this);
        transaction.commit();
    }

    private void addClick() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.activity_tasks, TaskCreateFragment.newInstance(id, null));
        transaction.remove(this);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setVisibilities(boolean isEmpty) {
        if (isEmpty) {
            expandableListView.setVisibility(View.INVISIBLE);
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            expandableListView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.INVISIBLE);
        }
        progressBar.setVisibility(View.INVISIBLE);
        addButton.setVisibility(View.VISIBLE);
    }

    private void deleteDialog(final Task task) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Delete task \"" + task.getDescription() + "\"?");
        builder.setPositiveButton(ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Notifier.removeAlarm(getActivity(), (int) task.getTimeStamp());
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("users").child(id).child(task.getId());
                myRef.setValue(null);
            }
        });
        builder.setNegativeButton(cancel, null);
        builder.setCancelable(true);
        builder.show();
    }
}
