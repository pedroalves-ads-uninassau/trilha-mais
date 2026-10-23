package com.trilhamais.backend.dto;

import java.time.LocalDateTime;

/**
 * DTO de uma mensagem do historico do chat com a IA.
 */
public class ChatMessageResponse {

    private Long id;

    private Long subjectId;

    private String role;

    private String content;

    private LocalDateTime createdAt;

    public ChatMessageResponse() {
    }

    public ChatMessageResponse(Long id, Long subjectId, String role, String content, LocalDateTime createdAt) {
        this.id = id;
        this.subjectId = subjectId;
        this.role = role;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
