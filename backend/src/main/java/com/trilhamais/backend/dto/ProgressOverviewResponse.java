package com.trilhamais.backend.dto;

import java.util.List;

/**
 * DTO consolidado da tela Progresso: media geral, contadores, desempenho por materia e destaques.
 */
public class ProgressOverviewResponse {

    private double overallAccuracyRate;

    private int evaluationsCount;

    private int topicsStudied;

    private int streakDays;

    private List<SubjectPerformanceResponse> subjects;

    private SubjectPerformanceResponse bestSubject;

    private SubjectPerformanceResponse needsReview;

    public ProgressOverviewResponse() {
    }

    public ProgressOverviewResponse(double overallAccuracyRate, int evaluationsCount, int topicsStudied, int streakDays, List<SubjectPerformanceResponse> subjects, SubjectPerformanceResponse bestSubject, SubjectPerformanceResponse needsReview) {
        this.overallAccuracyRate = overallAccuracyRate;
        this.evaluationsCount = evaluationsCount;
        this.topicsStudied = topicsStudied;
        this.streakDays = streakDays;
        this.subjects = subjects;
        this.bestSubject = bestSubject;
        this.needsReview = needsReview;
    }

    public double getOverallAccuracyRate() {
        return overallAccuracyRate;
    }

    public void setOverallAccuracyRate(double overallAccuracyRate) {
        this.overallAccuracyRate = overallAccuracyRate;
    }

    public int getEvaluationsCount() {
        return evaluationsCount;
    }

    public void setEvaluationsCount(int evaluationsCount) {
        this.evaluationsCount = evaluationsCount;
    }

    public int getTopicsStudied() {
        return topicsStudied;
    }

    public void setTopicsStudied(int topicsStudied) {
        this.topicsStudied = topicsStudied;
    }

    public int getStreakDays() {
        return streakDays;
    }

    public void setStreakDays(int streakDays) {
        this.streakDays = streakDays;
    }

    public List<SubjectPerformanceResponse> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<SubjectPerformanceResponse> subjects) {
        this.subjects = subjects;
    }

    public SubjectPerformanceResponse getBestSubject() {
        return bestSubject;
    }

    public void setBestSubject(SubjectPerformanceResponse bestSubject) {
        this.bestSubject = bestSubject;
    }

    public SubjectPerformanceResponse getNeedsReview() {
        return needsReview;
    }

    public void setNeedsReview(SubjectPerformanceResponse needsReview) {
        this.needsReview = needsReview;
    }
}
