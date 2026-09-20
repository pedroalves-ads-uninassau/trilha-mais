package com.trilhamais.backend.controller;

import com.trilhamais.backend.dto.AiAskRequest;
import com.trilhamais.backend.dto.AiExplanationResponse;
import com.trilhamais.backend.dto.AiSummarizeRequest;
import com.trilhamais.backend.dto.AiSummarizeResponse;
import com.trilhamais.backend.model.User;
import com.trilhamais.backend.service.GroqAiService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST para integracao inteligente de estudos via Groq Cloud (Llama 3.3).
 * Oferece assistencia de tutoria, esclarecimento de duvidas e sintese de conteudos.
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final GroqAiService groqAiService;

    public AiController(GroqAiService groqAiService) {
        this.groqAiService = groqAiService;
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
}
