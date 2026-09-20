package com.trilhamais.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public class QuestionRequest {

    @NotBlank(message = "O enunciado da questão é obrigatório.")
    private String text;

    private String explanation;

    @NotEmpty(message = "A questão deve conter alternativas.")
    @Size(min = 2, max = 6, message = "A questão deve conter entre 2 e 6 alternativas.")
    @Valid
    private List<OptionRequest> options;

    public QuestionRequest() {
    }

    public QuestionRequest(String text, String explanation, List<OptionRequest> options) {
        this.text = text;
        this.explanation = explanation;
        this.options = options;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public List<OptionRequest> getOptions() {
        return options;
    }

    public void setOptions(List<OptionRequest> options) {
        this.options = options;
    }
}
