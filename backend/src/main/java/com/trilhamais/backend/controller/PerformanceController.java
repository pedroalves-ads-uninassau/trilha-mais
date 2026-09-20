package com.trilhamais.backend.controller;

import com.trilhamais.backend.dto.PerformanceRecommendationResponse;
import com.trilhamais.backend.dto.PerformanceSummaryResponse;
import com.trilhamais.backend.dto.TopicPerformanceResponse;
import com.trilhamais.backend.model.User;
import com.trilhamais.backend.service.PerformanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST para o Modulo de Resultados, Desempenho e Recomendacoes Inteligentes.
 * Todos os endpoints requerem autenticacao Bearer JWT e calculam metricas estritas do estudante.
 */
@RestController
@RequestMapping("/api/performance")
public class PerformanceController {

    private final PerformanceService performanceService;

    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    /**
     * Retorna o resumo geral de desempenho (total de simulados, total de questoes, nota media e aproveitamento).
     * GET /api/performance/summary
     */
    @GetMapping("/summary")
    public ResponseEntity<PerformanceSummaryResponse> getSummary(@AuthenticationPrincipal User user) {
        PerformanceSummaryResponse response = performanceService.getPerformanceSummary(user);
        return ResponseEntity.ok(response);
    }

    /**
     * Retorna a analise detalhada de desempenho por assunto, ordenando os pontos de dificuldade prioritarios.
     * GET /api/performance/topics
     */
    @GetMapping("/topics")
    public ResponseEntity<List<TopicPerformanceResponse>> getTopicPerformances(@AuthenticationPrincipal User user) {
        List<TopicPerformanceResponse> responses = performanceService.getTopicPerformances(user);
        return ResponseEntity.ok(responses);
    }

    /**
     * Retorna recomendacoes personalizadas de estudo com base nas fragilidades do estudante,
     * incluindo lista de materiais cadastrados e plano didatico gerado por IA.
     * GET /api/performance/recommendations
     */
    @GetMapping("/recommendations")
    public ResponseEntity<PerformanceRecommendationResponse> getRecommendations(@AuthenticationPrincipal User user) {
        PerformanceRecommendationResponse response = performanceService.getRecommendations(user);
        return ResponseEntity.ok(response);
    }
}
