package com.trilhamais.backend.dto;

public class OptionResponse {

    private Long id;
    private String text;

    public OptionResponse() {
    }

    public OptionResponse(Long id, String text) {
        this.id = id;
        this.text = text;
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
}
