package com.trilhamais.backend.controller;

import com.trilhamais.backend.dto.QuestionDetailResponse;
import com.trilhamais.backend.dto.QuestionRequest;
import com.trilhamais.backend.model.User;
import com.trilhamais.backend.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    /**
     * Cadastra uma nova questao vinculada a um topico/assunto do estudante.
     * POST /api/topics/{topicId}/questions
     */
    @PostMapping("/topics/{topicId}/questions")
    public ResponseEntity<QuestionDetailResponse> createQuestion(
            @PathVariable Long topicId,
            @Valid @RequestBody QuestionRequest request,
            @AuthenticationPrincipal User user) {
        QuestionDetailResponse response = questionService.createQuestion(user, topicId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lista todas as questoes de um determinado topico.
     * GET /api/topics/{topicId}/questions
     */
    @GetMapping("/topics/{topicId}/questions")
    public ResponseEntity<List<QuestionDetailResponse>> getQuestionsByTopic(
            @PathVariable Long topicId,
            @AuthenticationPrincipal User user) {
        List<QuestionDetailResponse> questions = questionService.getQuestionsByTopic(user, topicId);
        return ResponseEntity.ok(questions);
    }

    /**
     * Obtem os detalhes de uma questao especifica.
     * GET /api/questions/{id}
     */
    @GetMapping("/questions/{id}")
    public ResponseEntity<QuestionDetailResponse> getQuestionById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        QuestionDetailResponse question = questionService.getQuestionById(user, id);
        return ResponseEntity.ok(question);
    }

    /**
     * Remove uma questao.
     * DELETE /api/questions/{id}
     */
    @DeleteMapping("/questions/{id}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        questionService.deleteQuestion(user, id);
        return ResponseEntity.noContent().build();
    }
}
