package com.example.portable.firebasetests.fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.portable.firebasetests.Notifier;
import com.example.portable.firebasetests.PredefinedTags;
import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.adapters.SubTaskAdapter;
import com.example.portable.firebasetests.adapters.TagAdapter;
import com.example.portable.firebasetests.listeners.OnMyItemLongClickListener;
import com.example.portable.firebasetests.model.SubTask;
import com.example.portable.firebasetests.model.Tag;
import com.example.portable.firebasetests.model.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import static android.R.string.cancel;
import static android.R.string.ok;

public class TaskCreateFragment extends Fragment {
    private static final String ID_ARG = "id", TASK_ARG = "task";
    private String userId;
    private Task task;
    private EditText descriptionEdit;
    private Button okButton, addSubTaskButton;
    private TextView dateTextView, timeTextView;
    private Spinner tagSpinner;
    private boolean timeSpecified = false;
    private RecyclerView subTasksRecycleView;

    public TaskCreateFragment() {
        // Required empty public constructor
    }

    public static TaskCreateFragment newInstance(String userId, Task task) {
        TaskCreateFragment fragment = new TaskCreateFragment();
        Bundle args = new Bundle();
        args.putString(ID_ARG, userId);
        if (task != null) {
            args.putSerializable(TASK_ARG, task);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            userId = getArguments().getString(ID_ARG);
            task = (Task) getArguments().getSerializable(TASK_ARG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task_create, container, false);
        descriptionEdit = (EditText) rootView.findViewById(R.id.taskDescriptionEdit);
        dateTextView = (TextView) rootView.findViewById(R.id.dateTextView);
        tagSpinner = (Spinner) rootView.findViewById(R.id.tagSpinner);
        timeTextView = (TextView) rootView.findViewById(R.id.timeTextView);
        timeTextView = (TextView) rootView.findViewById(R.id.timeTextView);
        okButton = (Button) rootView.findViewById(R.id.addTaskButton);
        subTasksRecycleView = (RecyclerView) rootView.findViewById(R.id.subTasksRecyclerView);
        addSubTaskButton = (Button) rootView.findViewById(R.id.addSubTaskButton);
        initInterface();
        return rootView;
    }

    private void initInterface() {
        tagSpinner.setAdapter(new TagAdapter(getActivity(), PredefinedTags.getTags()));
        if (task == null) {
            task = new Task();
            task.setId(userId + "_task_" + System.currentTimeMillis());
            dateTextView.setTextColor(Color.RED);
            timeTextView.setTextColor(Color.RED);
            task.setTimeStamp(System.currentTimeMillis());
        } else {
            descriptionEdit.setText(task.getDescription());
            initDateAndTime();
            timeSpecified = task.isTimeSpecified();
            tagSpinner.setSelection(PredefinedTags.getTags().indexOf(task.getTag()));
        }
        final SubTaskAdapter subTaskAdapter = new SubTaskAdapter(task.getSubTasks());

        subTaskAdapter.setLongClickListener(new OnMyItemLongClickListener() {
            @Override
            public void onLongClick(int index) {
                openDeletingDialog(subTaskAdapter.getItem(index));
            }
        });
        subTasksRecycleView.setAdapter(subTaskAdapter);
        subTasksRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.setDescription(descriptionEdit.getText().toString());
                if (canComplete()) {
                    saveTask();
                    getActivity().onBackPressed();
                }
            }
        });
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePicker();
            }
        });
        addSubTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddSubTaskDialog();
            }
        });
    }

    private void openDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.setTimeInMillis(task.getTimeStamp());
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                if (dateIsCorrect(calendar)) {
                    task.setTimeStamp(calendar.getTimeInMillis());
                    dateTextView.setText(task.getDateString());
                    dateTextView.setTextColor(Color.BLACK);
                } else {
                    showErrorToast("day of that week");
                    dateTextView.setText(R.string.date);
                    dateTextView.setTextColor(Color.RED);
                }
            }
        };
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        new DatePickerDialog(getActivity(), listener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private boolean dateIsCorrect(Calendar calendar) {
        Calendar now = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        now.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.WEEK_OF_YEAR) == now.get(Calendar.WEEK_OF_YEAR);
    }

    private void openTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.setTimeInMillis(task.getTimeStamp());
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                task.setTimeStamp(calendar.getTimeInMillis());
                timeTextView.setText(task.getTimeString());
                timeTextView.setTextColor(Color.BLACK);
                timeSpecified = true;
            }
        };
        calendar.setTimeInMillis(task.getTimeStamp());
        new TimePickerDialog(getActivity(), listener,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }


    private void initDateAndTime() {
        dateTextView.setText(task.getDateString());
        if (task.isTimeSpecified()) {
            timeTextView.setText(task.getTimeString());
        } else {
            timeTextView.setTextColor(Color.RED);
        }
    }

    private void saveTask() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        task.setTag((Tag) tagSpinner.getSelectedItem());
        task.setTimeSpecified(timeSpecified);
        if (timeSpecified && task.getTimeStamp() > System.currentTimeMillis()) {
            Notifier.removeAlarm(getActivity(), (int) task.getTimeStamp());
            Notifier.setAlarm(task, getActivity());
        }
        myRef.child(userId).child(task.getId()).setValue(task);
        for (SubTask subTask : task.getSubTasks()) {
            myRef = database.getReference("users").child(userId).child(task.getId()).child("subTasks")
                    .child(subTask.getId());
            myRef.setValue(subTask);
        }
    }

    private boolean canComplete() {
        if (task.getDescription().length() < 1) {
            showErrorToast("description");
            return false;
        }
        if (dateTextView.getText().toString().equals(getString(R.string.date))) {
            showErrorToast("date");
            return false;
        }
        if (task.getSubTasks().size() == 0) {
            showErrorToast("subtasks");
            return false;
        }
        return true;

    }

    private void showErrorToast(String cause) {
        Toast toast = Toast.makeText(getActivity(), "You should choose " + cause, Toast.LENGTH_LONG);
        toast.getView().setBackgroundColor(Color.RED);
        toast.show();
    }

    private void openAddSubTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText editText = new EditText(getActivity());
        editText.setHint(R.string.description);
        builder.setView(editText);
        builder.setMessage("Subtask's description:");
        builder.setPositiveButton(ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editText.getText().toString().length() > 0) {
                    SubTask subtask = new SubTask(editText.getText().toString());
                    subtask.setId(task.getId() + "_subtask_" + System.currentTimeMillis());
                    task.getSubTasks().add(subtask);
                }
            }
        });
        builder.setNegativeButton(cancel, null);
        builder.setCancelable(true);
        builder.show();
    }

    private void openDeletingDialog(final SubTask subTask) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Delete \"" + subTask.getDescription() + "\" subtask?");
        builder.setPositiveButton(ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("users").child(userId).child(task.getId()).child("subTasks").child(subTask.getId());
                myRef.setValue(null);
                task.getSubTasks().remove(subTask);
                subTasksRecycleView.getAdapter().notifyDataSetChanged();
            }
        });
        builder.setCancelable(true);
        builder.setNegativeButton(cancel, null);
        builder.show();
    }
}
