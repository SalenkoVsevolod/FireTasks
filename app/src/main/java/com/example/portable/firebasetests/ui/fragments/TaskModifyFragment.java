package com.example.portable.firebasetests.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.TagsColors;
import com.example.portable.firebasetests.core.Notifier;
import com.example.portable.firebasetests.core.Preferences;
import com.example.portable.firebasetests.model.Remind;
import com.example.portable.firebasetests.model.SubTask;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.network.FirebaseManager;
import com.example.portable.firebasetests.ui.adapters.ReminderAdapter;
import com.example.portable.firebasetests.ui.adapters.SubTaskAdapter;
import com.example.portable.firebasetests.ui.adapters.TagAdapter;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static android.R.string.cancel;

public class TaskModifyFragment extends Fragment implements View.OnClickListener {
    public static final String TASK_MODIFY_TAG = "modify";
    private static final String TASK_ARG = "task";
    private static final List<String> PRIORITIES = Arrays.asList("Law", "Normal", "High", "Urgent");
    private EditText descriptionEdit, nameEdit;
    private Button okButton, addSubTaskButton;
    private Spinner tagSpinner;
    private RecyclerView subTasksRecycleView, remindersRecyclerView;
    private CheckBox vibro;
    private Task task;
    private TimePicker reminderTime;
    private TextView soundTV;
    private Uri sound;

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
        nameEdit = (EditText) rootView.findViewById(R.id.nameET);
        descriptionEdit = (EditText) rootView.findViewById(R.id.taskDescriptionEdit);
        tagSpinner = (Spinner) rootView.findViewById(R.id.tagSpinner);
        okButton = (Button) rootView.findViewById(R.id.addTaskButton);
        subTasksRecycleView = (RecyclerView) rootView.findViewById(R.id.subTasksRecyclerView);
        addSubTaskButton = (Button) rootView.findViewById(R.id.addSubTaskButton);
        remindersRecyclerView = (RecyclerView) rootView.findViewById(R.id.reminder_recycler);
        rootView.findViewById(R.id.add_reminder_imv).setOnClickListener(this);
        configViewsForClosingKeyBord(rootView);
        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (task.getId() != null) {
            descriptionEdit.setText(task.getDescription());
            nameEdit.setText(task.getName());
        }
        tagSpinner.setAdapter(new TagAdapter(getActivity(), TagsColors.getTags()));
        initSubtasksRecyclerView();
        tagSpinner.setSelection((int) task.getTagIndex());
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.setName(nameEdit.getText().toString());
                task.setDescription(descriptionEdit.getText().toString());
                if (canComplete()) {
                    saveTask();
                    getActivity().onBackPressed();
                }
            }
        });
        addSubTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editSubtask(null);
            }
        });
        remindersRecyclerView.setAdapter(new ReminderAdapter(task.getReminds(), new ReminderAdapter.OnRemindClickListener() {
            @Override
            public void onClick(Remind remind) {
                showReminderAddDialog(remind);
            }
        }));
        remindersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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

    private void showErrorToast(String cause) {
        Toast toast = Toast.makeText(getActivity(), "You should choose " + cause, Toast.LENGTH_LONG);
        toast.getView().setBackgroundColor(Color.RED);
        toast.show();
    }

    private boolean canComplete() {
        if (task.getName().length() == 0) {
            showErrorToast("name");
            return false;
        }
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
                editSubtask(subTask);
            }
        });
        subTasksRecycleView.setAdapter(subTaskAdapter);
        subTasksRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_reminder_imv:
                showReminderAddDialog(null);
                break;
            case R.id.sound_tv:
                chooseSound();
                break;
        }
    }

    private void showReminderAddDialog(final Remind r) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.reminder_add, null);
        soundTV = (TextView) view.findViewById(R.id.sound_tv);
        soundTV.setOnClickListener(this);
        reminderTime = (TimePicker) view.findViewById(R.id.reminder_time_picker);
        reminderTime.setIs24HourView(true);
        vibro = (CheckBox) view.findViewById(R.id.reminder_vibro);
        final Remind reminder = r == null ? new Remind() : r;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setCancelable(true);

        if (r != null) {
            soundTV.setText(RingtoneManager.getRingtone(getActivity(), Uri.parse(r.getSound())).getTitle(getActivity()));
            vibro.setChecked(r.isVibro());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                reminderTime.setHour(r.getCalendar().get(Calendar.HOUR_OF_DAY));
                reminderTime.setMinute(r.getCalendar().get(Calendar.MINUTE));
            } else {
                reminderTime.setCurrentHour(r.getCalendar().get(Calendar.HOUR_OF_DAY));
                reminderTime.setCurrentMinute(r.getCalendar().get(Calendar.MINUTE));
            }
        }

        final int index = task.getReminds().indexOf(r);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                reminder.setTimeStamp(task.getTimeStamp());
                Log.i("remind", "task timestamp set: " + reminder.getId() + ":" + reminder.getCalendar());
                int hour, minute;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    hour = reminderTime.getHour();
                    minute = reminderTime.getMinute();
                } else {
                    hour = reminderTime.getCurrentHour();
                    minute = reminderTime.getCurrentMinute();
                }
                reminder.getCalendar().set(Calendar.HOUR_OF_DAY, hour);
                reminder.getCalendar().set(Calendar.MINUTE, minute);
                Log.i("remind", "hour and minute set: " + reminder.getId() + ":" + reminder.getCalendar());
                if (reminder.getTimeStamp() > System.currentTimeMillis()) {
                    if (sound != null) {
                        reminder.setSound(sound.toString());
                    } else {
                        reminder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());
                    }
                    reminder.setVibro(vibro.isChecked());
                    if (r == null) {
                        reminder.setId("reminder_" + System.currentTimeMillis());
                        task.getReminds().add(reminder);
                        remindersRecyclerView.getAdapter().notifyItemInserted(task.getReminds().size());
                    } else {
                        task.getReminds().set(index, reminder);
                        remindersRecyclerView.getAdapter().notifyItemChanged(index);
                    }
                    Log.i("remind", "on done: " + reminder.getId() + ":" + reminder.getCalendar());
                } else {
                    showErrorToast("time in future");
                }
            }
        });
        builder.setNegativeButton(cancel, null);
        builder.show();
    }

    private void chooseSound() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        String ringtone = Preferences.getInstance().readLastRingtone();
        if (ringtone != null) {
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(ringtone));
        }
        this.startActivityForResult(intent, 5);
    }

    private void editSubtask(final SubTask subTask) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.subtask_editor, null);
        final EditText editText = (EditText) view.findViewById(R.id.subtask_name);
        Spinner spinner = (Spinner) view.findViewById(R.id.priority_spinner);
        spinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, PRIORITIES));
        Button button = (Button) view.findViewById(R.id.delete_subtask);
        if (subTask != null) {
            button.setVisibility(View.VISIBLE);
            editText.setText(subTask.getDescription());
            //TODO set priority here
        }
        builder.setView(view);
        builder.setCancelable(true);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editText.getText().toString().length() > 0) {
                    if (subTask == null) {
                        SubTask s = new SubTask();
                        s.setDescription(editText.getText().toString());
                        s.setId("" + System.currentTimeMillis());
                        task.getSubTasks().add(s);
                        subTasksRecycleView.getAdapter().notifyItemInserted(task.getSubTasks().indexOf(s));
                        //TODO priority
                    } else {
                        int index = task.getSubTasks().indexOf(subTask);
                        subTask.setDescription(editText.getText().toString());
                        //TODO priority
                        task.getSubTasks().set(index, subTask);
                        subTasksRecycleView.getAdapter().notifyItemChanged(index);
                    }
                }
            }
        });
        final AlertDialog dialog = builder.show();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = task.getSubTasks().indexOf(subTask);
                task.getSubTasks().remove(subTask);
                subTasksRecycleView.getAdapter().notifyItemRemoved(pos);
                dialog.dismiss();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 5) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                sound = uri;
                soundTV.setText(RingtoneManager.getRingtone(getActivity(), sound).getTitle(getActivity()));
                // Preferences.getInstance().writeLastRingtone(uri.toString());
                // soundTextView.setText(RingtoneManager.getRingtone(getActivity(), uri).getTitle(getActivity()));
            } else {
                soundTV.setText("No sound");
            }
        }
    }

    private void saveTask() {
        if (task.getId() == null) {
            task.setId(Preferences.getInstance().readUserId() + "_task_" + System.currentTimeMillis());
        }
        for (Remind r : task.getReminds()) {
            Log.i("remind", r.getId() + ":" + r);
        }
        task.setTagIndex(tagSpinner.getSelectedItemPosition());
        Notifier.removeAlarms(task);
        Notifier.setAlarms(task);
        FirebaseManager.getInstance().saveTask(task);
    }
}
