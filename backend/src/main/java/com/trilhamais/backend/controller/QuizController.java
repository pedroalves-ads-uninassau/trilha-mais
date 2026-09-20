package com.trilhamais.backend.controller;

import com.trilhamais.backend.dto.*;
import com.trilhamais.backend.model.User;
import com.trilhamais.backend.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    /**
     * Gera um novo simulado dinâmico (com 10 ou 15 questoes) para um topico ou materia.
     * POST /api/quizzes/generate
     */
    @PostMapping("/generate")
    public ResponseEntity<QuizDetailResponse> generateQuiz(
            @Valid @RequestBody QuizGenerateRequest request,
            @AuthenticationPrincipal User user) {
        QuizDetailResponse response = quizService.generateQuiz(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Obtem os dados de um simulado para resolucao (oculta gabarito e explicacoes).
     * GET /api/quizzes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuizDetailResponse> getQuizForExecution(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        QuizDetailResponse response = quizService.getQuizForExecution(user, id);
        return ResponseEntity.ok(response);
    }

    /**
     * Submete as respostas do simulado, computando correcao automatica, nota e gabarito.
     * POST /api/quizzes/{id}/submit
     */
    @PostMapping("/{id}/submit")
    public ResponseEntity<QuizResultResponse> submitQuiz(
            @PathVariable Long id,
            @Valid @RequestBody QuizSubmitRequest request,
            @AuthenticationPrincipal User user) {
        QuizResultResponse response = quizService.submitQuiz(user, id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Consulta o resultado e gabarito comentado de um simulado ja finalizado.
     * GET /api/quizzes/{id}/result
     */
    @GetMapping("/{id}/result")
    public ResponseEntity<QuizResultResponse> getQuizResult(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        QuizResultResponse response = quizService.getQuizResult(user, id);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista o historico de simulados gerados e finalizados do estudante autenticado.
     * GET /api/quizzes
     */
    @GetMapping
    public ResponseEntity<List<QuizResponse>> listUserQuizzes(@AuthenticationPrincipal User user) {
        List<QuizResponse> quizzes = quizService.listUserQuizzes(user);
        return ResponseEntity.ok(quizzes);
    }
}
