package com.trilhamais.backend.dto;

import java.time.LocalDateTime;

/**
 * DTO para exibicao de informacoes de um Assunto (Topic).
 */
public class TopicResponse {

    private Long id;
    private String name;
    private String description;
    private Long subjectId;
    private String subjectName;
    private LocalDateTime createdAt;

    public TopicResponse() {
    }

    public TopicResponse(Long id, String name, String description, Long subjectId, String subjectName, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
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

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
