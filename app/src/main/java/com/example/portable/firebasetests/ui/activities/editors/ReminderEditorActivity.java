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

    private Remind mRemind;
    private CheckBox mVibrationCheckBox;
    private TimePicker mTimePicker;
    private TextView mSoundTextView;
    private int mResultCode;

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
        mTimePicker = (TimePicker) findViewById(R.id.reminder_time_picker);
        mTimePicker.setIs24HourView(true);
        mSoundTextView = (TextView) findViewById(R.id.sound_tv);
        mVibrationCheckBox = (CheckBox) findViewById(R.id.reminder_vibro);
        mRemind = (Remind) getIntent().getSerializableExtra(REMINDER);

        if (mRemind.getSound() != null) {
            mSoundTextView.setText(getSoundContent(getRingtoneTitle(Uri.parse(mRemind.getSound()))));
        } else {
            mSoundTextView.setText(getSoundContent(getString(R.string.no_sound)));
        }
        mVibrationCheckBox.setChecked(mRemind.isVibro());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTimePicker.setHour(mRemind.getCalendar().get(Calendar.HOUR_OF_DAY));
            mTimePicker.setMinute(mRemind.getCalendar().get(Calendar.MINUTE));
        } else {
            mTimePicker.setCurrentHour(mRemind.getCalendar().get(Calendar.HOUR_OF_DAY));
            mTimePicker.setCurrentMinute(mRemind.getCalendar().get(Calendar.MINUTE));
        }
        mSoundTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseSound();
            }
        });
    }

    private void chooseSound() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.select_tone));
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, mRemind.getSound() == null ? null : Uri.parse(mRemind.getSound()));
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
                        mRemind.setSound(uri.toString());
                        mSoundTextView.setText(getSoundContent(getRingtoneTitle(uri)));
                    } else {
                        mSoundTextView.setText(getSoundContent(getString(R.string.no_sound)));
                    }
                }
                break;
        }
    }

    @Override
    protected boolean assembleEntityAndProceed() {
        int hour, minute;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = mTimePicker.getHour();
            minute = mTimePicker.getMinute();
        } else {
            hour = mTimePicker.getCurrentHour();
            minute = mTimePicker.getCurrentMinute();
        }
        mRemind.getCalendar().set(Calendar.HOUR_OF_DAY, hour);
        mRemind.getCalendar().set(Calendar.MINUTE, minute);
        if (mRemind.getCalendar().getTimeInMillis() <= System.currentTimeMillis()) {
            showToast(getString(R.string.choose_time_in_future), false);
            return false;
        }
        mRemind.setVibro(mVibrationCheckBox.isChecked());
        if (mRemind.getId() == null) {
            mRemind.setId("reminder_" + System.currentTimeMillis());
            mResultCode = CREATE;
        } else {
            mResultCode = UPDATE;
        }
        return true;
    }

    protected int getmResultCode() {
        return mResultCode;
    }

    @Override
    protected Remind getResultData() {
        return mRemind;
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