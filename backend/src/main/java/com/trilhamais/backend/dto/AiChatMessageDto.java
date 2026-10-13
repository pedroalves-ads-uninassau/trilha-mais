package com.trilhamais.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class AiChatMessageDto {

    @NotBlank
    private String role; // "user" ou "assistant" ou "system"

    @NotBlank
    private String content;

    public AiChatMessageDto() {
    }

    public AiChatMessageDto(String role, String content) {
        this.role = role;
        this.content = content;
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
}
