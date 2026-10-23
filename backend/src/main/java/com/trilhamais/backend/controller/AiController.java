package com.trilhamais.backend.controller;

import com.trilhamais.backend.dto.AiAskRequest;
import com.trilhamais.backend.dto.AiExplanationResponse;
import com.trilhamais.backend.dto.AiSummarizeRequest;
import com.trilhamais.backend.dto.AiSummarizeResponse;
import com.trilhamais.backend.dto.ChatMessageResponse;
import com.trilhamais.backend.model.User;
import com.trilhamais.backend.service.ChatHistoryService;
import com.trilhamais.backend.service.GroqAiService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST para integracao inteligente de estudos via Groq Cloud (Llama 3.3).
 * Oferece assistencia de tutoria, esclarecimento de duvidas e sintese de conteudos.
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final GroqAiService groqAiService;
    private final ChatHistoryService chatHistoryService;

    public AiController(GroqAiService groqAiService, ChatHistoryService chatHistoryService) {
        this.groqAiService = groqAiService;
        this.chatHistoryService = chatHistoryService;
    }

    /**
     * Tira duvidas acadêmicas com a IA com base no assunto e materiais do estudante.
     * POST /api/ai/ask
     */
    @PostMapping("/ask")
    public ResponseEntity<AiExplanationResponse> askQuestion(
            @Valid @RequestBody AiAskRequest request,
            @AuthenticationPrincipal User user) {
        AiExplanationResponse response = groqAiService.askQuestion(request, user);
        return ResponseEntity.ok(response);
    }

    /**
     * Gera um resumo e topicos-chave de revisao a partir de um texto ou anotação.
     * POST /api/ai/summarize
     */
    @PostMapping("/summarize")
    public ResponseEntity<AiSummarizeResponse> summarize(
            @Valid @RequestBody AiSummarizeRequest request,
            @AuthenticationPrincipal User user) {
        AiSummarizeResponse response = groqAiService.summarize(request, user);
        return ResponseEntity.ok(response);
    }

    /**
     * Chat conversacional interativo com a IA com histórico de mensagens.
     * POST /api/ai/chat
     */
    @PostMapping("/chat")
    public ResponseEntity<com.trilhamais.backend.dto.AiChatResponse> chat(
            @Valid @RequestBody com.trilhamais.backend.dto.AiChatRequest request,
            @AuthenticationPrincipal User user) {
        String reply = groqAiService.chat(request.getMessages());
        chatHistoryService.saveExchange(user, request.getSubjectId(), request.getMessages(), reply);
        return ResponseEntity.ok(new com.trilhamais.backend.dto.AiChatResponse(reply));
    }

    /**
     * Historico salvo do chat (opcionalmente filtrado pela materia).
     * GET /api/ai/chat/history?subjectId=1
     */
    @GetMapping("/chat/history")
    public ResponseEntity<List<ChatMessageResponse>> getChatHistory(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Long subjectId) {
        return ResponseEntity.ok(chatHistoryService.getHistory(user, subjectId));
    }

    /**
     * Apaga o historico do chat (todo ou so de uma materia).
     * DELETE /api/ai/chat/history?subjectId=1
     */
    @DeleteMapping("/chat/history")
    public ResponseEntity<Void> clearChatHistory(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Long subjectId) {
        chatHistoryService.clearHistory(user, subjectId);
        return ResponseEntity.noContent().build();
    }
}
