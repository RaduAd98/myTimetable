package com.example.mytimetable.models;

public class ModuleInfoModel extends BaseItemListModel {

    private String moduleName;
    private String roomName;
    private String participant;
    private String timeInterval;
    private String date;
    private String semester;
    private String documentId;
    private String groupName;

    public ModuleInfoModel() {
    }

    public ModuleInfoModel(String moduleName, String roomName,
                           String participant, String timeInterval,
                           String date, String semester,
                           String groupName, String documentId) {
        this.moduleName = moduleName;
        this.roomName = roomName;
        this.participant = participant;
        this.timeInterval = timeInterval;
        this.date = date;
        this.documentId = documentId;
        this.semester = semester;
        this.groupName = groupName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getParticipant() {
        return participant;
    }

    public void setParticipant(String participant) {
        this.participant = participant;
    }

    public String getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(String timeInterval) {
        this.timeInterval = timeInterval;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
