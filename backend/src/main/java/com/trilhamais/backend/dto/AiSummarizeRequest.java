package com.trilhamais.backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para solicitação de resumo inteligente via IA.
 */
public class AiSummarizeRequest {

    @NotBlank(message = "O conteúdo a ser resumido é obrigatório.")
    private String content;

    private String topicName;

    public AiSummarizeRequest() {
    }

    public AiSummarizeRequest(String content, String topicName) {
        this.content = content;
        this.topicName = topicName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}
