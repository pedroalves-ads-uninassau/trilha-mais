package com.trilhamais.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO detalhado para exibicao de uma Materia com a lista completa de seus assuntos (topics).
 */
public class SubjectDetailResponse {

    private Long id;
    private String name;
    private String description;
    private List<TopicResponse> topics;
    private LocalDateTime createdAt;

    public SubjectDetailResponse() {
    }

    public SubjectDetailResponse(Long id, String name, String description, List<TopicResponse> topics, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
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
