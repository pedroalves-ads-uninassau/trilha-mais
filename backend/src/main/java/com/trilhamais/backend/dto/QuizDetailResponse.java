package com.trilhamais.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class QuizDetailResponse {

    private Long id;
    private String title;
    private Long topicId;
    private String topicName;
    private Long subjectId;
    private String subjectName;
    private int totalQuestions;
    private String status;
    private LocalDateTime createdAt;
    private List<QuestionResponse> questions;

    public QuizDetailResponse() {
    }

    public QuizDetailResponse(Long id, String title, Long topicId, String topicName, Long subjectId, String subjectName,
                              int totalQuestions, String status, LocalDateTime createdAt, List<QuestionResponse> questions) {
        this.id = id;
        this.title = title;
        this.topicId = topicId;
        this.topicName = topicName;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.totalQuestions = totalQuestions;
        this.status = status;
        this.createdAt = createdAt;
        this.questions = questions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<QuestionResponse> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionResponse> questions) {
        this.questions = questions;
    }
}
