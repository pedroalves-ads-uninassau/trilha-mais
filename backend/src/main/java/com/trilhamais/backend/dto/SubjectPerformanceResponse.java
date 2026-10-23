package com.trilhamais.backend.dto;

/**
 * DTO com o aproveitamento do estudante em uma materia (tela Progresso).
 */
public class SubjectPerformanceResponse {

    private Long subjectId;

    private String subjectName;

    private String icon;

    private String colorKey;

    private String primaryColor;

    private int questionsAnswered;

    private int correctAnswers;

    private double accuracyRate;

    private boolean hasData;

    public SubjectPerformanceResponse() {
    }

    public SubjectPerformanceResponse(Long subjectId, String subjectName, String icon, String colorKey, String primaryColor, int questionsAnswered, int correctAnswers, double accuracyRate, boolean hasData) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.icon = icon;
        this.colorKey = colorKey;
        this.primaryColor = primaryColor;
        this.questionsAnswered = questionsAnswered;
        this.correctAnswers = correctAnswers;
        this.accuracyRate = accuracyRate;
        this.hasData = hasData;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColorKey() {
        return colorKey;
    }

    public void setColorKey(String colorKey) {
        this.colorKey = colorKey;
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
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

    public double getAccuracyRate() {
        return accuracyRate;
    }

    public void setAccuracyRate(double accuracyRate) {
        this.accuracyRate = accuracyRate;
    }

    public boolean isHasData() {
        return hasData;
    }

    public void setHasData(boolean hasData) {
        this.hasData = hasData;
    }
}
