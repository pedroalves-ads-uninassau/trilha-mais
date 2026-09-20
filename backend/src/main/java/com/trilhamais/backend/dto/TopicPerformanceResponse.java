package com.trilhamais.backend.dto;

public class TopicPerformanceResponse {

    private Long topicId;
    private String topicName;
    private Long subjectId;
    private String subjectName;
    private int questionsAnswered;
    private int correctAnswers;
    private int wrongAnswers;
    private double accuracyRate;
    private String masteryLevel; // CRITICAL (< 60%), REGULAR (60-79%), MASTERED (>= 80%)

    public TopicPerformanceResponse() {
    }

    public TopicPerformanceResponse(Long topicId, String topicName, Long subjectId, String subjectName,
                                  int questionsAnswered, int correctAnswers, int wrongAnswers,
                                  double accuracyRate, String masteryLevel) {
        this.topicId = topicId;
        this.topicName = topicName;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.questionsAnswered = questionsAnswered;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
        this.accuracyRate = accuracyRate;
        this.masteryLevel = masteryLevel;
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

    public int getQuestionsAnswered() {
        return questionsAnswered;
    }

    public void setQuestionsAnswered(int questionsAnswered) {
        this.questionsAnswered = questionsAnswered;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(int wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public double getAccuracyRate() {
        return accuracyRate;
    }

    public void setAccuracyRate(double accuracyRate) {
        this.accuracyRate = accuracyRate;
    }

    public String getMasteryLevel() {
        return masteryLevel;
    }

    public void setMasteryLevel(String masteryLevel) {
        this.masteryLevel = masteryLevel;
    }
}
