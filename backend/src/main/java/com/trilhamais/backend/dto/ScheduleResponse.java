package com.trilhamais.backend.dto;

import java.time.LocalDate;
public class ScheduleResponse {

    private Long id;
    private String title;
    private String subjectName;
    private String colorKey;
    private String icon;
    private int daysAhead;
    private String scheduledTime;
    private boolean completed;

    // Data real do compromisso e ligacao com materia/assunto (daysAhead e recalculado a partir da data)
    private LocalDate scheduledDate;
    private Long subjectId;
    private Long topicId;

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

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }
}
