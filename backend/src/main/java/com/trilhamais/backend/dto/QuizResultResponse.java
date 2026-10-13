package com.trilhamais.backend.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class QuizResultResponse {

    private Long quizId;
    private String title;
    private String status;
    private int totalQuestions;
    private int correctAnswers;
    private double score;
    private double percentage;
    private LocalDateTime completedAt;
    private List<QuizCorrectionItemResponse> results = new ArrayList<>();
    private List<String> fortes = new ArrayList<>();
    private List<String> fracos = new ArrayList<>();

    public QuizResultResponse() {
    }

    public QuizResultResponse(Long quizId, String title, String status, int totalQuestions, int correctAnswers,
                              double score, double percentage, LocalDateTime completedAt,
                              List<QuizCorrectionItemResponse> results) {
        this.quizId = quizId;
        this.title = title;
        this.status = status;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.score = score;
        this.percentage = percentage;
        this.completedAt = completedAt;
        this.results = results != null ? results : new ArrayList<>();
    }

    public QuizResultResponse(Long quizId, String title, String status, int totalQuestions, int correctAnswers,
                              double score, double percentage, LocalDateTime completedAt,
                              List<QuizCorrectionItemResponse> results, List<String> fortes, List<String> fracos) {
        this.quizId = quizId;
        this.title = title;
        this.status = status;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.score = score;
        this.percentage = percentage;
        this.completedAt = completedAt;
        this.results = results != null ? results : new ArrayList<>();
        this.fortes = fortes != null ? fortes : new ArrayList<>();
        this.fracos = fracos != null ? fracos : new ArrayList<>();
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public List<QuizCorrectionItemResponse> getResults() {
        return results;
    }

    public void setResults(List<QuizCorrectionItemResponse> results) {
        this.results = results;
    }

    public List<String> getFortes() {
        return fortes;
    }

    public void setFortes(List<String> fortes) {
        this.fortes = fortes;
    }

    public List<String> getFracos() {
        return fracos;
    }

    public void setFracos(List<String> fracos) {
        this.fracos = fracos;
    }
}
