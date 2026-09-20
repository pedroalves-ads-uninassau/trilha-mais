package com.trilhamais.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class QuizSubmitRequest {

    @NotEmpty(message = "A lista de respostas não pode estar vazia.")
    @Valid
    private List<AnswerItemRequest> answers;

    public QuizSubmitRequest() {
    }

    public QuizSubmitRequest(List<AnswerItemRequest> answers) {
        this.answers = answers;
    }

    public List<AnswerItemRequest> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerItemRequest> answers) {
        this.answers = answers;
    }
}
