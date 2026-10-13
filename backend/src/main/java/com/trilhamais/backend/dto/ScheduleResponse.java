package com.trilhamais.backend.dto;

public class ScheduleResponse {

    private Long id;
    private String title;
    private String subjectName;
    private String colorKey;
    private String icon;
    private int daysAhead;
    private String scheduledTime;
    private boolean completed;

    public ScheduleResponse() {
    }

    public ScheduleResponse(Long id, String title, String subjectName, String colorKey, String icon,
                            int daysAhead, String scheduledTime, boolean completed) {
        this.id = id;
        this.title = title;
        this.subjectName = subjectName;
        this.colorKey = colorKey;
        this.icon = icon;
        this.daysAhead = daysAhead;
        this.scheduledTime = scheduledTime;
        this.completed = completed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
