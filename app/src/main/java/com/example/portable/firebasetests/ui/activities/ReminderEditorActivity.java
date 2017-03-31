package com.example.portable.firebasetests.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.Remind;

import java.util.Calendar;

public class ReminderEditorActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String REMINDER = "remind";
    public static final int CREATE = 1, UPDATE = 2, CANCEL = 3, DELETE = 4;
    private static final String TIMESTAMP = "timestamp";
    private static final int SOUND_CODE = 5;
    private Remind remind;
    private CheckBox vibro;
    private TimePicker reminderTime;
    private TextView soundTV;
    private Uri sound;
    private long timestamp;
    private Button deleteButton;

    public static Intent getStarterIntent(Context context, Remind remind, long taskTimeStamp) {
        Intent starter = new Intent(context, ReminderEditorActivity.class);
        starter.putExtra(REMINDER, remind);
        starter.putExtra(TIMESTAMP, taskTimeStamp);
        return starter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_editor);
        reminderTime = (TimePicker) findViewById(R.id.reminder_time_picker);
        reminderTime.setIs24HourView(true);
        soundTV = (TextView) findViewById(R.id.sound_tv);
        vibro = (CheckBox) findViewById(R.id.reminder_vibro);
        deleteButton = (Button) findViewById(R.id.delete_reminder);

        soundTV.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        findViewById(R.id.save_button).setOnClickListener(this);

        remind = (Remind) getIntent().getSerializableExtra(REMINDER);
        timestamp = getIntent().getLongExtra(TIMESTAMP, -1);
        if (remind != null) {
            deleteButton.setVisibility(View.VISIBLE);
            soundTV.setText(remind.getSound() == null ? "No sound" : RingtoneManager.getRingtone(this, Uri.parse(remind.getSound())).getTitle(this));
            vibro.setChecked(remind.isVibro());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                reminderTime.setHour(remind.getCalendar().get(Calendar.HOUR_OF_DAY));
                reminderTime.setMinute(remind.getCalendar().get(Calendar.MINUTE));
            } else {
                reminderTime.setCurrentHour(remind.getCalendar().get(Calendar.HOUR_OF_DAY));
                reminderTime.setCurrentMinute(remind.getCalendar().get(Calendar.MINUTE));
            }
        } else {
            soundTV.setText("No sound");
        }
    }

    private void saveClick() {
        final Remind rem = remind == null ? new Remind() : remind;
        rem.setTimeStamp(timestamp);
        int hour, minute;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = reminderTime.getHour();
            minute = reminderTime.getMinute();
        } else {
            hour = reminderTime.getCurrentHour();
            minute = reminderTime.getCurrentMinute();
        }
        rem.getCalendar().set(Calendar.HOUR_OF_DAY, hour);
        rem.getCalendar().set(Calendar.MINUTE, minute);
        if (sound != null) {
            rem.setSound(sound.toString());
        } else {
            rem.setSound(null);
            soundTV.setText("No sound");
        }
        rem.setVibro(vibro.isChecked());
        if (remind == null) {
            rem.setId("reminder_" + System.currentTimeMillis());
            returnRemind(CREATE, rem);
        } else {
            returnRemind(UPDATE, rem);
        }

    }

    private void chooseSound() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, sound);
        this.startActivityForResult(intent, SOUND_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SOUND_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    if (uri != null) {
                        sound = uri;
                        soundTV.setText(RingtoneManager.getRingtone(this, sound).getTitle(this));
                        // Preferences.getInstance().writeLastRingtone(uri.toString());
                        // soundTextView.setText(RingtoneManager.getRingtone(getActivity(), uri).getTitle(getActivity()));
                    } else {
                        soundTV.setText("No sound");
                    }
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sound_tv:
                chooseSound();
                break;
            case R.id.save_button:
                saveClick();
                break;
            case R.id.delete_reminder:
                returnRemind(DELETE, remind);
                break;
        }
    }

    private void returnRemind(int code, Remind r) {
        Intent intent = new Intent();
        intent.putExtra(REMINDER, r);
        setResult(code, intent);
        finish();
    }
}
