package com.example.mytimetable.models;

public class StudentModel {

    private String studentId;
    private String studentName;
    private boolean isSelected = false;
    private boolean isSelectedAndSaved = false;

    public StudentModel() {
    }

    public StudentModel(String studentId, String studentName, boolean isSelected) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.isSelected = isSelected;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelectedAndSaved() {
        return isSelectedAndSaved;
    }

    public void setSelectedAndSaved(boolean selectedAndSaved) {
        isSelectedAndSaved = selectedAndSaved;
    }
}
