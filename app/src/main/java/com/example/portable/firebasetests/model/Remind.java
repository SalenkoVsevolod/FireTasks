package com.example.portable.firebasetests.model;

import com.example.portable.firebasetests.utils.StringUtils;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Salenko Vsevolod on 21.03.2017.
 */

public class Remind extends FirebaseEntity implements Serializable {
    private String sound;
    private boolean vibro;
    private Calendar calendar;
    private String taskId;
    private String title;
    private String message;

    public Remind() {
        calendar = Calendar.getInstance();
    }

    public Remind(HashMap<String, Object> map) {
        this();
        calendar.setTimeInMillis((long) map.get("timeStamp"));
        vibro = (boolean) map.get("vibro");
        sound = (String) map.get("sound");
        title = (String) map.get("title");
        message = (String) map.get("message");
        taskId = (String) map.get("taskId");
    }


    public long getTimeStamp() {
        return calendar.getTimeInMillis();
    }

    public void setTimeStamp(long timeStamp) {
        calendar.setTimeInMillis(timeStamp);
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public boolean isVibro() {
        return vibro;
    }

    public void setVibro(boolean vibro) {
        this.vibro = vibro;
    }

    @Exclude
    public Calendar getCalendar() {
        return calendar;
    }

    @Exclude
    @Override
    public String toString() {
        return StringUtils.getTimeString(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }

    public long round() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(calendar.getTimeInMillis());
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Remind)) {
            return false;
        }
        Remind r = (Remind) obj;
        return r.getId().equals(id);
    }

    @Override
    public void init(FirebaseEntity entity) {
        Remind remind = (Remind) entity;
        calendar.setTimeInMillis(remind.getTimeStamp());
        sound = remind.getSound();
        vibro = remind.isVibro();
        taskId = remind.getTaskId();
        title = remind.getTitle();
        message = remind.getMessage();
    }

    @Override
    public boolean isIdentical(FirebaseEntity entity) {
        Remind remind = (Remind) entity;
        return getTimeStamp() == remind.getTimeStamp()
                && vibro == remind.isVibro()
                && isSoundIdentical(remind.getSound())
                && title.equals(remind.getTitle())
                && message.equals(remind.getMessage());
    }

    private boolean isSoundIdentical(String inputSound) {
        if (sound == null) {
            return inputSound == null;
        } else {
            return sound.equals(inputSound);
        }
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
