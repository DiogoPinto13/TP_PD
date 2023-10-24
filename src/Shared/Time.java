package Shared;

import java.io.Serializable;

public class Time implements Serializable {
    private int Hour, Minute;

    public Time(int hour, int minute) {
        Hour = hour;
        Minute = minute;
    }

    public int getHour() {
        return Hour;
    }
    public void setHour(int hour) {
        Hour = hour;
    }
    public int getMinute() {
        return Minute;
    }
    public void setMinute(int minute) {
        Minute = minute;
    }
    @Override
    public String toString() {
        return Hour + ":" + Minute;
    }
}
