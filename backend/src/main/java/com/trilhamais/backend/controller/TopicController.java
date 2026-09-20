package com.trilhamais.backend.controller;

import com.trilhamais.backend.dto.TopicRequest;
import com.trilhamais.backend.dto.TopicResponse;
import com.trilhamais.backend.model.User;
import com.trilhamais.backend.service.TopicService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gestao de Assuntos ou Conteudos de estudo (Topic).
 * Permite operacoes aninhadas a partir de materias ou consultas diretas de tópicos.
 */
@RestController
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    /**
     * Adiciona um novo assunto a uma materia especifica do estudante.
     * POST /api/subjects/{subjectId}/topics
     */
    @PostMapping("/api/subjects/{subjectId}/topics")
    public ResponseEntity<TopicResponse> createTopic(
            @PathVariable Long subjectId,
            @Valid @RequestBody TopicRequest request,
            @AuthenticationPrincipal User user) {
        TopicResponse response = topicService.createTopic(subjectId, request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lista todos os assuntos cadastrados em uma materia especifica.
     * GET /api/subjects/{subjectId}/topics
     */
    @GetMapping("/api/subjects/{subjectId}/topics")
    public ResponseEntity<List<TopicResponse>> getTopicsBySubject(
            @PathVariable Long subjectId,
            @AuthenticationPrincipal User user) {
        List<TopicResponse> topics = topicService.getTopicsBySubject(subjectId, user);
        return ResponseEntity.ok(topics);
    }

    /**
     * Retorna os detalhes de um assunto especifico.
     * GET /api/topics/{id}
     */
    @GetMapping("/api/topics/{id}")
    public ResponseEntity<TopicResponse> getTopicById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        TopicResponse response = topicService.getTopicById(id, user);
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza dados de um assunto existente.
     * PUT /api/topics/{id}
     */
    @PutMapping("/api/topics/{id}")
    public ResponseEntity<TopicResponse> updateTopic(
            @PathVariable Long id,
            @Valid @RequestBody TopicRequest request,
            @AuthenticationPrincipal User user) {
        TopicResponse response = topicService.updateTopic(id, request, user);
        return ResponseEntity.ok(response);
    }

    /**
     * Remove um assunto especifico.
     * DELETE /api/topics/{id}
     */
    @DeleteMapping("/api/topics/{id}")
    public ResponseEntity<Void> deleteTopic(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        topicService.deleteTopic(id, user);
        return ResponseEntity.noContent().build();
    }
}
