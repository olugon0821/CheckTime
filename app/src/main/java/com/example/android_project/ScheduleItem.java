package com.example.android_project;

public class ScheduleItem {
    private int id;
    private String name;
    private String date;
    private boolean isChecked;

    public ScheduleItem(int id, String name, String date, boolean isChecked) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.isChecked = isChecked;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
