package com.example.portable.firebasetests.model;

import com.example.portable.firebasetests.utils.StringUtils;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Salenko Vsevolod on 21.03.2017.
 */

public class Remind implements Serializable {

    private String sound;
    private boolean vibro;
    private Calendar calendar;

    public Remind() {
        sound = "";
        calendar = Calendar.getInstance();
    }

    public Remind(HashMap<String, Object> map) {
        this();
        calendar.setTimeInMillis((long) map.get("timeStamp"));
        vibro = (boolean) map.get("vibro");

    }


    public long getTimeStamp() {
        return calendar.getTimeInMillis();
    }

    @Exclude
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

    public void setTimeStamp(long timeStamp) {
        calendar.setTimeInMillis(timeStamp);
    }

    @Exclude
    public Calendar getCalendar() {
        return calendar;
    }

    @Exclude
    @Override
    public String toString() {
        return StringUtils.formatNumber(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + StringUtils.formatNumber(calendar.get(Calendar.MINUTE));
    }

    public void round() {
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
}
