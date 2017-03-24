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

import java.util.Calendar;

import static android.R.string.cancel;
import static android.R.string.ok;

public class TaskModifyFragment extends Fragment implements View.OnClickListener {
    public static final String TASK_MODIFY_TAG = "modify";
    private static final String TASK_ARG = "task";
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
                openAddSubTaskDialog();
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


    private void saveTask() {
        if (task.getId() == null) {
            task.setId(Preferences.getInstance().readUserId() + "_task_" + System.currentTimeMillis());
        }
        task.setTagIndex(tagSpinner.getSelectedItemPosition());
        Notifier.removeAlarms(task);
        Notifier.setAlarms(task);
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
                    subtask.setId("" + System.currentTimeMillis());
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
                openSubtaskDeleteDialog(subTask);
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
        final Remind remind = new Remind();
        View view = getActivity().getLayoutInflater().inflate(R.layout.reminder_add, null);
        soundTV = (TextView) view.findViewById(R.id.sound_tv);
        soundTV.setOnClickListener(this);
        reminderTime = (TimePicker) view.findViewById(R.id.reminder_time_picker);
        reminderTime.setIs24HourView(true);
        vibro = (CheckBox) view.findViewById(R.id.reminder_vibro);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setCancelable(true);
        final int index = task.getReminds().indexOf(r);
        if (r == null) {
            remind.setTimeStamp(task.getTimeStamp());
            remind.setId("reminder_" + remind.getTimeStamp());
            remind.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());
        } else {
            remind.setId(r.getId());
            remind.setSound(r.getSound());
            remind.setTimeStamp(r.getTimeStamp());
            remind.setVibro(r.isVibro());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                reminderTime.setHour(remind.getCalendar().get(Calendar.HOUR_OF_DAY));
                reminderTime.setMinute(remind.getCalendar().get(Calendar.MINUTE));
            } else {
                reminderTime.setCurrentHour(remind.getCalendar().get(Calendar.HOUR_OF_DAY));
                reminderTime.setCurrentMinute(remind.getCalendar().get(Calendar.MINUTE));
            }
        }
        soundTV.setText(RingtoneManager.getRingtone(getActivity(), Uri.parse(remind.getSound())).getTitle(getActivity()));
        vibro.setChecked(remind.isVibro());
        reminderTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                remind.getCalendar().set(Calendar.HOUR_OF_DAY, i);
                remind.getCalendar().set(Calendar.MINUTE, i1);
            }
        });

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("rem", task.getReminds().toString());
                if (sound != null) {
                    remind.setSound(sound.toString());
                }
                remind.setVibro(vibro.isChecked());
                if (r == null) {
                    task.getReminds().add(remind);
                    remindersRecyclerView.getAdapter().notifyItemInserted(task.getReminds().size());
                } else {
                    task.getReminds().set(index, remind);
                    remindersRecyclerView.getAdapter().notifyItemChanged(index);
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
}
