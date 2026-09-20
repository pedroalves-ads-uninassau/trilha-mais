package com.trilhamais.backend.dto;

import java.util.List;

public class PerformanceSummaryResponse {

    private int totalQuizzesCompleted;
    private int totalQuestionsAnswered;
    private int totalCorrectAnswers;
    private double averageScore;
    private double overallAccuracyRate;
    private List<Double> recentScores;

    public PerformanceSummaryResponse() {
    }

    public PerformanceSummaryResponse(int totalQuizzesCompleted, int totalQuestionsAnswered, int totalCorrectAnswers,
                                      double averageScore, double overallAccuracyRate, List<Double> recentScores) {
        this.totalQuizzesCompleted = totalQuizzesCompleted;
        this.totalQuestionsAnswered = totalQuestionsAnswered;
        this.totalCorrectAnswers = totalCorrectAnswers;
        this.averageScore = averageScore;
        this.overallAccuracyRate = overallAccuracyRate;
        this.recentScores = recentScores;
    }

    public int getTotalQuizzesCompleted() {
        return totalQuizzesCompleted;
    }

    public void setTotalQuizzesCompleted(int totalQuizzesCompleted) {
        this.totalQuizzesCompleted = totalQuizzesCompleted;
    }

    public int getTotalQuestionsAnswered() {
        return totalQuestionsAnswered;
    }

    public void setTotalQuestionsAnswered(int totalQuestionsAnswered) {
        this.totalQuestionsAnswered = totalQuestionsAnswered;
    }

    public int getTotalCorrectAnswers() {
        return totalCorrectAnswers;
    }

    public void setTotalCorrectAnswers(int totalCorrectAnswers) {
        this.totalCorrectAnswers = totalCorrectAnswers;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public double getOverallAccuracyRate() {
        return overallAccuracyRate;
    }

    public void setOverallAccuracyRate(double overallAccuracyRate) {
        this.overallAccuracyRate = overallAccuracyRate;
    }

    public List<Double> getRecentScores() {
        return recentScores;
    }

    public void setRecentScores(List<Double> recentScores) {
        this.recentScores = recentScores;
    }
}
