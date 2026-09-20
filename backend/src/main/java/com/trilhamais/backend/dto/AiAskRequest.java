package com.trilhamais.backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO com a pergunta e contexto acadêmico enviados à IA.
 */
public class AiAskRequest {

    @NotBlank(message = "A dúvida ou pergunta é obrigatória.")
    private String question;

    private Long topicId;
    private Long materialId;
    private String customContext;

    public AiAskRequest() {
    }

    public AiAskRequest(String question, Long topicId, Long materialId, String customContext) {
        this.question = question;
        this.topicId = topicId;
        this.materialId = materialId;
        this.customContext = customContext;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public String getCustomContext() {
        return customContext;
    }

    public void setCustomContext(String customContext) {
        this.customContext = customContext;
    }
}
