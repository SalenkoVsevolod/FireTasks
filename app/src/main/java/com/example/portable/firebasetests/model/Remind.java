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

    public Remind() {
        calendar = Calendar.getInstance();
    }

    public Remind(HashMap<String, Object> map) {
        this();
        calendar.setTimeInMillis((long) map.get("timeStamp"));
        vibro = (boolean) map.get("vibro");
        sound = (String) map.get("sound");
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
        Remind r = (Remind) obj;
        return r.getId().equals(id);
    }

    @Override
    public void init(FirebaseEntity entity) {
        Remind remind = (Remind) entity;
        calendar.setTimeInMillis(remind.getTimeStamp());
        sound = remind.getSound();
        vibro = remind.isVibro();
    }

    @Override
    public boolean isIdentical(FirebaseEntity entity) {
        Remind remind = (Remind) entity;
        return getTimeStamp() == remind.getTimeStamp() && vibro == remind.isVibro() && isSoundIdentical(remind.getSound());
    }

    private boolean isSoundIdentical(String inputSound) {
        if (sound == null) {
            return inputSound == null;
        } else {
            return sound.equals(inputSound);
        }
    }
}
