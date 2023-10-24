package Shared;

import java.io.Serializable;

public class Event implements Serializable {
    private String designation;
    private String place;
    private String date;
    private Time time;
    private long presenceCode;

    public Event(String designation, String place, String date, Time time, long presenceCode) {
        this.designation = designation;
        this.place = place;
        this.date = date;
        this.time = time;
        this.presenceCode = presenceCode;
    }

    public String getDesignation() {
        return designation;
    }
    public void setDesignation(String designation) {
        this.designation = designation;
    }
    public String getPlace() {
        return place;
    }
    public void setPlace(String place) {
        this.place = place;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public Time getTime() {
        return time;
    }
    public void setTime(Time time) {
        this.time = time;
    }
    public long getPresenceCode() {
        return presenceCode;
    }
    public void setPresenceCode(long presenceCode) {
        this.presenceCode = presenceCode;
    }
}
