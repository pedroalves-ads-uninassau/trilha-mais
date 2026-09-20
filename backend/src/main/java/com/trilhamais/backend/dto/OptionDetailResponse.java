package com.trilhamais.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OptionDetailResponse {

    private Long id;
    private String text;

    @JsonProperty("isCorrect")
    private boolean isCorrect;

    public OptionDetailResponse() {
    }

    public OptionDetailResponse(Long id, String text, boolean isCorrect) {
        this.id = id;
        this.text = text;
        this.isCorrect = isCorrect;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        isCorrect = correct;
    }
}
