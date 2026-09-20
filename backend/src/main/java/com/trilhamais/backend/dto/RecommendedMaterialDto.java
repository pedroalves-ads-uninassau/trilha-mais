package com.trilhamais.backend.dto;

public class RecommendedMaterialDto {

    private Long id;
    private String title;
    private String type;
    private Long topicId;
    private String topicName;

    public RecommendedMaterialDto() {
    }

    public RecommendedMaterialDto(Long id, String title, String type, Long topicId, String topicName) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.topicId = topicId;
        this.topicName = topicName;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
