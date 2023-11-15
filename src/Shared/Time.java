package Shared;

import java.io.Serializable;

public class Time implements Serializable {
    private int Hour, Minute, Day, Month, Year;

    public Time(int day, int month, int year, int hour, int minute) {
        Hour = hour;
        Minute = minute;
        Day = day;
        Month = month;
        Year = year;
    }

    public int getDay() {
        return Day;
    }

    public void setDay(int day) {
        Day = day;
    }

    public int getMonth() {
        return Month;
    }

    public void setMonth(int month) {
        Month = month;
    }

    public int getYear() {
        return Year;
    }

    public void setYear(int year) {
        Year = year;
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
        return getYear() + "-" + getMonth() + "-" + getDay() + " " + getHour() + ":" + getMinute();
    }
    public String toStringHourMinute(){
        return getHour() + ":" + getMinute();
    }
    public String toStringDay(){
        return getYear() + "-" + getMonth() + "-" + getDay();
    }
}
