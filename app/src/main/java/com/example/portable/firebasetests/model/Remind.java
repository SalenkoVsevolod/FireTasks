package com.example.portable.firebasetests.model;

import com.example.portable.firebasetests.utils.StringUtils;
import com.google.firebase.database.Exclude;

import java.util.HashMap;

/**
 * Created by Salenko Vsevolod on 21.03.2017.
 */

public class Remind {
    private String id;
    private int hour, minute;
    private String sound;
    private boolean vibro;

    public Remind() {
        sound = "";
    }

    public Remind(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public Remind(HashMap<String, Object> map) {
        this();
        //TODO add time here
        sound = (String) map.get("sound");
        vibro = (boolean) map.get("vibro");

    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
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

    @Override
    public String toString() {
        return StringUtils.formatNumber(hour) + ":" + StringUtils.formatNumber(minute);
    }
}
