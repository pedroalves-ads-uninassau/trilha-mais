package com.trilhamais.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class AiChatRequest {

    @NotEmpty(message = "O histórico de mensagens não pode estar vazio.")
    private List<AiChatMessageDto> messages;

    public AiChatRequest() {
    }

    public AiChatRequest(List<AiChatMessageDto> messages) {
        this.messages = messages;
    }

    public List<AiChatMessageDto> getMessages() {
        return messages;
    }

    public void setMessages(List<AiChatMessageDto> messages) {
        this.messages = messages;
    }
}
