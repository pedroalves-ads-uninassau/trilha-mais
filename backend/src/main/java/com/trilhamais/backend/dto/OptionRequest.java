package com.trilhamais.backend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class OptionRequest {

    @NotBlank(message = "O texto da alternativa é obrigatório.")
    private String text;

    @JsonProperty("isCorrect")
    @JsonAlias({"correct", "is_correct", "isCorrect"})
    private boolean isCorrect;

    public OptionRequest() {
    }

    public OptionRequest(String text, boolean isCorrect) {
        this.text = text;
        this.isCorrect = isCorrect;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @JsonProperty("isCorrect")
    public boolean isCorrect() {
        return isCorrect;
    }

    @JsonProperty("isCorrect")
    public void setCorrect(boolean correct) {
        this.isCorrect = correct;
    }

    public void setIsCorrect(boolean correct) {
        this.isCorrect = correct;
    }
}
