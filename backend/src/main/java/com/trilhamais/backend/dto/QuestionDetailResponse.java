package com.trilhamais.backend.dto;

import java.util.List;

public class QuestionDetailResponse {

    private Long id;
    private String text;
    private String explanation;
    private Long topicId;
    private String topicName;
    private List<OptionDetailResponse> options;

    public QuestionDetailResponse() {
    }

    public QuestionDetailResponse(Long id, String text, String explanation, Long topicId, String topicName, List<OptionDetailResponse> options) {
        this.id = id;
        this.text = text;
        this.explanation = explanation;
        this.topicId = topicId;
        this.topicName = topicName;
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

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public List<OptionDetailResponse> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDetailResponse> options) {
        this.options = options;
    }
}
