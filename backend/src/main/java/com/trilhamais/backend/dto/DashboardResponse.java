package com.trilhamais.backend.dto;

import java.util.List;

public class DashboardResponse {

    private String userName;
    private int streakDays;
    private int overallProgressPercent;
    private String motivationMessage;

    private int totalSubjects;
    private int totalTopics;
    private int totalEvaluations;

    // Continuar de onde parou
    private String continueSubjectName;
    private String continueTopicName;
    private String continueColorKey;

    private List<SubjectResponse> subjects;

    public DashboardResponse() {
    }

    public DashboardResponse(String userName, int streakDays, int overallProgressPercent, String motivationMessage,
                             int totalSubjects, int totalTopics, int totalEvaluations, String continueSubjectName,
                             String continueTopicName, String continueColorKey, List<SubjectResponse> subjects) {
        this.userName = userName;
        this.streakDays = streakDays;
        this.overallProgressPercent = overallProgressPercent;
        this.motivationMessage = motivationMessage;
        this.totalSubjects = totalSubjects;
        this.totalTopics = totalTopics;
        this.totalEvaluations = totalEvaluations;
        this.continueSubjectName = continueSubjectName;
        this.continueTopicName = continueTopicName;
        this.continueColorKey = continueColorKey;
        this.subjects = subjects;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getStreakDays() {
        return streakDays;
    }

    public void setStreakDays(int streakDays) {
        this.streakDays = streakDays;
    }

    public int getOverallProgressPercent() {
        return overallProgressPercent;
    }

    public void setOverallProgressPercent(int overallProgressPercent) {
        this.overallProgressPercent = overallProgressPercent;
    }

    public String getMotivationMessage() {
        return motivationMessage;
    }

    public void setMotivationMessage(String motivationMessage) {
        this.motivationMessage = motivationMessage;
    }

    public int getTotalSubjects() {
        return totalSubjects;
    }

    public void setTotalSubjects(int totalSubjects) {
        this.totalSubjects = totalSubjects;
    }

    public int getTotalTopics() {
        return totalTopics;
    }

    public void setTotalTopics(int totalTopics) {
        this.totalTopics = totalTopics;
    }

    public int getTotalEvaluations() {
        return totalEvaluations;
    }

    public void setTotalEvaluations(int totalEvaluations) {
        this.totalEvaluations = totalEvaluations;
    }

    public String getContinueSubjectName() {
        return continueSubjectName;
    }

    public void setContinueSubjectName(String continueSubjectName) {
        this.continueSubjectName = continueSubjectName;
    }

    public String getContinueTopicName() {
        return continueTopicName;
    }

    public void setContinueTopicName(String continueTopicName) {
        this.continueTopicName = continueTopicName;
    }

    public String getContinueColorKey() {
        return continueColorKey;
    }

    public void setContinueColorKey(String continueColorKey) {
        this.continueColorKey = continueColorKey;
    }

    public List<SubjectResponse> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<SubjectResponse> subjects) {
        this.subjects = subjects;
    }
}
