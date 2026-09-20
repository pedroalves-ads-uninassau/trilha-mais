package com.trilhamais.backend.dto;

import com.trilhamais.backend.model.MaterialType;
import java.time.LocalDateTime;

/**
 * DTO para retorno de dados de um material de estudo.
 */
public class StudyMaterialResponse {

    private Long id;
    private String title;
    private String content;
    private MaterialType type;
    private Long topicId;
    private String topicName;
    private LocalDateTime createdAt;

    public StudyMaterialResponse() {
    }

    public StudyMaterialResponse(Long id, String title, String content, MaterialType type, Long topicId, String topicName, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.type = type;
        this.topicId = topicId;
        this.topicName = topicName;
        this.createdAt = createdAt;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MaterialType getType() {
        return type;
    }

    public void setType(MaterialType type) {
        this.type = type;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
