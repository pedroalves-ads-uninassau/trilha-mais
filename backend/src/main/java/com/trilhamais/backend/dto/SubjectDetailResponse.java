package com.trilhamais.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO detalhado para exibicao de uma Materia com seus tokens visuais, métricas e lista de assuntos.
 */
public class SubjectDetailResponse {

    private Long id;
    private String name;
    private String description;
    private String colorKey;
    private String primaryColor;
    private String icon;
    private int topicsCount;
    private int completedCount;
    private int inProgressCount;
    private int pendingCount;
    private int progress;
    private List<TopicResponse> topics;
    private LocalDateTime createdAt;

    public SubjectDetailResponse() {
    }

    public SubjectDetailResponse(Long id, String name, String description, String colorKey, String primaryColor,
                                 String icon, int topicsCount, int completedCount, int inProgressCount,
                                 int pendingCount, int progress, List<TopicResponse> topics, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.colorKey = colorKey;
        this.primaryColor = primaryColor;
        this.icon = icon;
        this.topicsCount = topicsCount;
        this.completedCount = completedCount;
        this.inProgressCount = inProgressCount;
        this.pendingCount = pendingCount;
        this.progress = progress;
        this.topics = topics;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColorKey() {
        return colorKey;
    }

    public void setColorKey(String colorKey) {
        this.colorKey = colorKey;
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getTopicsCount() {
        return topicsCount;
    }

    public void setTopicsCount(int topicsCount) {
        this.topicsCount = topicsCount;
    }

    public int getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(int completedCount) {
        this.completedCount = completedCount;
    }

    public int getInProgressCount() {
        return inProgressCount;
    }

    public void setInProgressCount(int inProgressCount) {
        this.inProgressCount = inProgressCount;
    }

    public int getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(int pendingCount) {
        this.pendingCount = pendingCount;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public List<TopicResponse> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicResponse> topics) {
        this.topics = topics;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
