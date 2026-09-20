package com.trilhamais.backend.dto;

import java.util.List;

/**
 * DTO com o resumo inteligente gerado pela IA e seus pontos-chave de revisão.
 */
public class AiSummarizeResponse {

    private String summary;
    private List<String> keyTakeaways;
    private String modelProvider;

    public AiSummarizeResponse() {
    }

    public AiSummarizeResponse(String summary, List<String> keyTakeaways, String modelProvider) {
        this.summary = summary;
        this.keyTakeaways = keyTakeaways;
        this.modelProvider = modelProvider;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getKeyTakeaways() {
        return keyTakeaways;
    }

    public void setKeyTakeaways(List<String> keyTakeaways) {
        this.keyTakeaways = keyTakeaways;
    }

    public String getModelProvider() {
        return modelProvider;
    }

    public void setModelProvider(String modelProvider) {
        this.modelProvider = modelProvider;
    }
}
