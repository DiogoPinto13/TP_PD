package Shared;

import java.util.ArrayList;

public class EventResult {
    private String columns;
    public ArrayList<String> events;

    public EventResult(String columns) {
        this.columns = columns;
        events = new ArrayList<String>();
    }

    public String getColumns() {
        return columns;
    }

    public void setColumns(String columns) {
        this.columns = columns;
    }
}
