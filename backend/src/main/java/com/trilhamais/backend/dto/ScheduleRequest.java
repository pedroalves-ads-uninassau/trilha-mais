package com.trilhamais.backend.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;

public class ScheduleRequest {

    @NotBlank(message = "O título é obrigatório.")
    private String title;

    private String subjectName;
    private String colorKey;
    private String icon;
    // Integer (e nao int) para aceitar JSON sem esse campo: o Jackson 3 usa o construtor e recusaria null em int
    private Integer daysAhead = 0;
    private String scheduledTime;

    // Opcionais: ligam o compromisso a materia/assunto cadastrados e definem a data exata (senao usa daysAhead)
    private Long subjectId;
    private Long topicId;
    private LocalDate scheduledDate;

    public ScheduleRequest() {
    }

    public ScheduleRequest(String title, String subjectName, String colorKey, String icon, Integer daysAhead, String scheduledTime) {
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
        return daysAhead != null ? daysAhead : 0;
    }

    public void setDaysAhead(Integer daysAhead) {
        this.daysAhead = daysAhead;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
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

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }
}
