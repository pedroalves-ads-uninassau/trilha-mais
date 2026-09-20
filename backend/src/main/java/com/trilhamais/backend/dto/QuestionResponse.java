package com.trilhamais.backend.dto;

import java.util.List;

public class QuestionResponse {

    private Long id;
    private String text;
    private List<OptionResponse> options;

    public QuestionResponse() {
    }

    public QuestionResponse(Long id, String text, List<OptionResponse> options) {
        this.id = id;
        this.text = text;
        this.options = options;
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

    public List<OptionResponse> getOptions() {
        return options;
    }

    public void setOptions(List<OptionResponse> options) {
        this.options = options;
    }
}
