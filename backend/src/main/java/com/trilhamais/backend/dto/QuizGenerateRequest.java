package com.trilhamais.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class QuizGenerateRequest {

    private Long topicId;
    private Long subjectId;

    @Min(value = 1, message = "O simulado deve conter no mínimo 1 questão.")
    @Max(value = 50, message = "O simulado pode conter no máximo 50 questões.")
    private Integer questionCount = 10;

    private String title;

    public QuizGenerateRequest() {
    }

    public QuizGenerateRequest(Long topicId, Long subjectId, Integer questionCount, String title) {
        this.topicId = topicId;
        this.subjectId = subjectId;
        this.questionCount = (questionCount != null && questionCount > 0) ? questionCount : 10;
        this.title = title;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
