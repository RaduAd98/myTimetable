package com.example.mytimetable.models;

public class TimeIntervalModel extends BaseItemListModel {

    private String timeInterval;

    public TimeIntervalModel() {
    }

    public TimeIntervalModel(String timeInterval) {
        this.timeInterval = timeInterval;
    }

    public String getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(String timeInterval) {
        this.timeInterval = timeInterval;
    }
}
