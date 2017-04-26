package com.example.portable.firebasetests.ui.activities.editors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.Remind;

import java.util.Calendar;

public class ReminderEditorActivity extends EditorActivity<Remind> {
    public static final String REMINDER = "remind";
    public static final int REQUEST_CODE = 138;
    private static final int SOUND_CODE = 5;
    private Remind remind;
    private CheckBox vibro;
    private TimePicker reminderTime;
    private TextView soundTV;
    private int resultCode;

    public static Intent getStarterIntent(Context context, @NonNull Remind remind) {
        Intent starter = new Intent(context, ReminderEditorActivity.class);
        starter.putExtra(REMINDER, remind);
        return starter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_editor);
        setListeners();
        reminderTime = (TimePicker) findViewById(R.id.reminder_time_picker);
        reminderTime.setIs24HourView(true);
        soundTV = (TextView) findViewById(R.id.sound_tv);
        vibro = (CheckBox) findViewById(R.id.reminder_vibro);
        remind = (Remind) getIntent().getSerializableExtra(REMINDER);

        if (remind.getSound() != null) {
            soundTV.setText(getSoundContent(getRingtoneTitle(Uri.parse(remind.getSound()))));
        } else {
            soundTV.setText(getSoundContent("No sound"));
        }
        vibro.setChecked(remind.isVibro());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            reminderTime.setHour(remind.getCalendar().get(Calendar.HOUR_OF_DAY));
            reminderTime.setMinute(remind.getCalendar().get(Calendar.MINUTE));
        } else {
            reminderTime.setCurrentHour(remind.getCalendar().get(Calendar.HOUR_OF_DAY));
            reminderTime.setCurrentMinute(remind.getCalendar().get(Calendar.MINUTE));
        }
        soundTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseSound();
            }
        });
    }

    private void chooseSound() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, remind.getSound() == null ? null : Uri.parse(remind.getSound()));
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
                        remind.setSound(uri.toString());
                        soundTV.setText(getSoundContent(getRingtoneTitle(uri)));
                    } else {
                        soundTV.setText(getSoundContent("No sound"));
                    }
                }
                break;
        }
    }

    @Override
    protected boolean assembleEntityAndProceed() {
        int hour, minute;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = reminderTime.getHour();
            minute = reminderTime.getMinute();
        } else {
            hour = reminderTime.getCurrentHour();
            minute = reminderTime.getCurrentMinute();
        }
        remind.getCalendar().set(Calendar.HOUR_OF_DAY, hour);
        remind.getCalendar().set(Calendar.MINUTE, minute);
        if (remind.getCalendar().getTimeInMillis() <= System.currentTimeMillis()) {
            showToast("Please choose time in future", false);
            return false;
        }
        remind.setVibro(vibro.isChecked());
        if (remind.getId() == null) {
            remind.setId("reminder_" + System.currentTimeMillis());
            resultCode = CREATE;
        } else {
            resultCode = UPDATE;
        }
        return true;
    }

    @Override
    protected int getResultCode() {
        return resultCode;
    }

    @Override
    protected Remind getResultData() {
        return remind;
    }

    private String getRingtoneTitle(Uri sound) {
        return RingtoneManager.getRingtone(this, sound).getTitle(this);
    }

    private SpannableString getSoundContent(String sound) {
        SpannableString content = new SpannableString(sound);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        return content;
    }

}