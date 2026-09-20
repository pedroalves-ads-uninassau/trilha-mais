package com.trilhamais.backend.dto;

import jakarta.validation.constraints.NotNull;

public class AnswerItemRequest {

    @NotNull(message = "O ID da questão é obrigatório.")
    private Long questionId;

    @NotNull(message = "O ID da alternativa selecionada é obrigatório.")
    private Long selectedOptionId;

    public AnswerItemRequest() {
    }

    public AnswerItemRequest(Long questionId, Long selectedOptionId) {
        this.questionId = questionId;
        this.selectedOptionId = selectedOptionId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getSelectedOptionId() {
        return selectedOptionId;
    }

    public void setSelectedOptionId(Long selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }
}
