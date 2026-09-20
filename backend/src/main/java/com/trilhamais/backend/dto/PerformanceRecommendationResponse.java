package com.trilhamais.backend.dto;

import java.util.List;

public class PerformanceRecommendationResponse {

    private List<TopicPerformanceResponse> criticalTopics;
    private List<RecommendedMaterialDto> recommendedMaterials;
    private String aiStudyPlan;
    private String aiProvider;

    public PerformanceRecommendationResponse() {
    }

    public PerformanceRecommendationResponse(List<TopicPerformanceResponse> criticalTopics,
                                             List<RecommendedMaterialDto> recommendedMaterials,
                                             String aiStudyPlan, String aiProvider) {
        this.criticalTopics = criticalTopics;
        this.recommendedMaterials = recommendedMaterials;
        this.aiStudyPlan = aiStudyPlan;
        this.aiProvider = aiProvider;
    }

    public List<TopicPerformanceResponse> getCriticalTopics() {
        return criticalTopics;
    }

    public void setCriticalTopics(List<TopicPerformanceResponse> criticalTopics) {
        this.criticalTopics = criticalTopics;
    }

    public List<RecommendedMaterialDto> getRecommendedMaterials() {
        return recommendedMaterials;
    }

    public void setRecommendedMaterials(List<RecommendedMaterialDto> recommendedMaterials) {
        this.recommendedMaterials = recommendedMaterials;
    }

    public String getAiStudyPlan() {
        return aiStudyPlan;
    }

    public void setAiStudyPlan(String aiStudyPlan) {
        this.aiStudyPlan = aiStudyPlan;
    }

    public String getAiProvider() {
        return aiProvider;
    }

    public void setAiProvider(String aiProvider) {
        this.aiProvider = aiProvider;
    }
}
