package com.cgp.actilife;

public class SleepReminder {
    private String day;
    private String time;

    public SleepReminder(String day, String time) {
        this.day = day;
        this.time = time;
    }

    public String getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
