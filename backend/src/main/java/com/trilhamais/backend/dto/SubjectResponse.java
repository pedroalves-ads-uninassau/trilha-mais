package com.trilhamais.backend.dto;

import java.time.LocalDateTime;

/**
 * DTO resumido para listagem de Materias com contagem de assuntos.
 */
public class SubjectResponse {

    private Long id;
    private String name;
    private String description;
    private int topicsCount;
    private LocalDateTime createdAt;

    public SubjectResponse() {
    }

    public SubjectResponse(Long id, String name, String description, int topicsCount, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.topicsCount = topicsCount;
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

    public int getTopicsCount() {
        return topicsCount;
    }

    public void setTopicsCount(int topicsCount) {
        this.topicsCount = topicsCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
