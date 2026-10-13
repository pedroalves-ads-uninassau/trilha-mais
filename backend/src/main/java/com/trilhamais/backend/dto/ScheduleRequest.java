package com.trilhamais.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class ScheduleRequest {

    @NotBlank(message = "O título é obrigatório.")
    private String title;

    private String subjectName;
    private String colorKey;
    private String icon;
    private int daysAhead = 0;
    private String scheduledTime;

    public ScheduleRequest() {
    }

    public ScheduleRequest(String title, String subjectName, String colorKey, String icon, int daysAhead, String scheduledTime) {
        this.title = title;
        this.subjectName = subjectName;
        this.colorKey = colorKey;
        this.icon = icon;
        this.daysAhead = daysAhead;
        this.scheduledTime = scheduledTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getColorKey() {
        return colorKey;
    }

    public void setColorKey(String colorKey) {
        this.colorKey = colorKey;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getDaysAhead() {
        return daysAhead;
    }

    public void setDaysAhead(int daysAhead) {
        this.daysAhead = daysAhead;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
}
