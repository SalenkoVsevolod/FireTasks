package com.example.portable.firebasetests.ui.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.TagsColors;
import com.example.portable.firebasetests.core.Notifier;
import com.example.portable.firebasetests.core.Preferences;
import com.example.portable.firebasetests.model.SubTask;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.network.FirebaseManager;
import com.example.portable.firebasetests.ui.adapters.SubTaskAdapter;
import com.example.portable.firebasetests.ui.adapters.TagAdapter;

import java.util.Calendar;

import static android.R.string.cancel;
import static android.R.string.ok;

public class TaskModifyFragment extends Fragment {
    public static final String TASK_MODIFY_TAG = "modify";
    private static final String TASK_ARG = "task";
    private EditText descriptionEdit;
    private Button okButton, addSubTaskButton;
    private TextView timeTextView;
    private Spinner tagSpinner;
    private RecyclerView subTasksRecycleView;
    private Task task;


    public TaskModifyFragment() {
        // Required empty public constructor
    }

    public static TaskModifyFragment newInstance(Task task) {
        TaskModifyFragment fragment = new TaskModifyFragment();
        Bundle args = new Bundle();
        args.putSerializable(TASK_ARG, task);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            task = (Task) getArguments().getSerializable(TASK_ARG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task_modify, container, false);
        descriptionEdit = (EditText) rootView.findViewById(R.id.taskDescriptionEdit);
        tagSpinner = (Spinner) rootView.findViewById(R.id.tagSpinner);
        timeTextView = (TextView) rootView.findViewById(R.id.timeTextView);
        timeTextView = (TextView) rootView.findViewById(R.id.timeTextView);
        okButton = (Button) rootView.findViewById(R.id.addTaskButton);
        subTasksRecycleView = (RecyclerView) rootView.findViewById(R.id.subTasksRecyclerView);
        addSubTaskButton = (Button) rootView.findViewById(R.id.addSubTaskButton);
        configViewsForClosingKeyBord(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (task.getId() != null) {
            descriptionEdit.setText(task.getDescription());
            if (task.isTimeSpecified()) {
                timeTextView.setText(task.getTimeString());
            }
        }
        tagSpinner.setAdapter(new TagAdapter(getActivity(), TagsColors.getTags()));
        initSubtasksRecyclerView();
        tagSpinner.setSelection((int) task.getTagIndex());
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
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });
        addSubTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddSubTaskDialog();
            }
        });

    }

    private void configViewsForClosingKeyBord(View rootView) {
        if (!(rootView instanceof EditText)) {
            rootView.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard();
                    return false;
                }
            });
        }
        if (rootView instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) rootView).getChildCount(); i++) {
                View innerView = ((ViewGroup) rootView).getChildAt(i);
                configViewsForClosingKeyBord(innerView);
            }
        }
    }


    private void hideKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getActivity().getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if (getActivity().getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void showTimePickerDialog() {
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
                task.setTimeSpecified();
            }
        };
        calendar.setTimeInMillis(task.getTimeStamp());
        new TimePickerDialog(getActivity(), listener,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void saveTask() {
        task.setId(Preferences.getInstance().readUserId() + "_task_" + System.currentTimeMillis());
        task.setTagIndex(tagSpinner.getSelectedItemPosition());
        if (task.isTimeSpecified() && task.getTimeStamp() > System.currentTimeMillis()) {
            Notifier.removeAlarm((int) task.getTimeStamp());
            Notifier.setAlarm(task);
        }
        FirebaseManager.getInstance().saveTask(task);
    }


    private void openAddSubTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText editText = new EditText(getActivity());
        builder.setView(editText);
        builder.setMessage("Subtask's description:");
        builder.setPositiveButton(ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editText.getText().toString().length() > 0) {
                    SubTask subtask = new SubTask(editText.getText().toString());
                    subtask.setId(task.getId() + "_subtask_" + System.currentTimeMillis());
                    task.getSubTasks().add(subtask);
                    subTasksRecycleView.getAdapter().notifyItemInserted(task.getSubTasks().indexOf(subtask));
                }
            }
        });
        builder.setNegativeButton(cancel, null);
        builder.setCancelable(true);
        builder.show();
    }

    private void openSubtaskDeleteDialog(final SubTask subTask) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Delete \"" + subTask.getDescription() + "\" subtask?");
        builder.setPositiveButton(ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseManager.getInstance().deleteSubtask(task.getCalendar().get(Calendar.WEEK_OF_YEAR), task.getId(), subTask.getId());
                int pos = task.getSubTasks().indexOf(subTask);
                task.getSubTasks().remove(subTask);
                subTasksRecycleView.getAdapter().notifyItemRemoved(pos);
            }
        });
        builder.setCancelable(true);
        builder.setNegativeButton(cancel, null);
        builder.show();
    }

    private void showErrorToast(String cause) {
        Toast toast = Toast.makeText(getActivity(), "You should choose " + cause, Toast.LENGTH_LONG);
        toast.getView().setBackgroundColor(Color.RED);
        toast.show();
    }

    private boolean canComplete() {
        if (task.getDescription().length() == 0) {
            showErrorToast("description");
            return false;
        }
        if (task.getSubTasks().size() == 0) {
            showErrorToast("subtasks");
            return false;
        }
        return true;

    }

    private void initSubtasksRecyclerView() {
        SubTaskAdapter subTaskAdapter = new SubTaskAdapter(task.getSubTasks(), new SubTaskAdapter.OnSubTaskClickListener() {
            @Override
            public void onClick(SubTask subTask) {
                openSubtaskDeleteDialog(subTask);
            }
        });
        subTasksRecycleView.setAdapter(subTaskAdapter);
        subTasksRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
