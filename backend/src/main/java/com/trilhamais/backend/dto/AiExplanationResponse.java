package com.trilhamais.backend.dto;

import java.util.List;

/**
 * DTO com a resposta didática gerada pela IA e perguntas sugeridas de aprofundamento.
 */
public class AiExplanationResponse {

    private String answer;
    private String topicName;
    private String modelProvider;
    private List<String> suggestedQuestions;

    public AiExplanationResponse() {
    }

    public AiExplanationResponse(String answer, String topicName, String modelProvider, List<String> suggestedQuestions) {
        this.answer = answer;
        this.topicName = topicName;
        this.modelProvider = modelProvider;
        this.suggestedQuestions = suggestedQuestions;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getModelProvider() {
        return modelProvider;
    }

    public void setModelProvider(String modelProvider) {
        this.modelProvider = modelProvider;
    }

    public List<String> getSuggestedQuestions() {
        return suggestedQuestions;
    }

    public void setSuggestedQuestions(List<String> suggestedQuestions) {
        this.suggestedQuestions = suggestedQuestions;
    }
}
