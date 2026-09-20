package com.trilhamais.backend.controller;

import com.trilhamais.backend.dto.StudyMaterialRequest;
import com.trilhamais.backend.dto.StudyMaterialResponse;
import com.trilhamais.backend.model.User;
import com.trilhamais.backend.service.StudyMaterialService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gestao de Materiais Didaticos vinculados aos assuntos de estudo.
 */
@RestController
public class StudyMaterialController {

    private final StudyMaterialService studyMaterialService;

    public StudyMaterialController(StudyMaterialService studyMaterialService) {
        this.studyMaterialService = studyMaterialService;
    }

    /**
     * Cadastra um novo material de estudo em um assunto.
     * POST /api/topics/{topicId}/materials
     */
    @PostMapping("/api/topics/{topicId}/materials")
    public ResponseEntity<StudyMaterialResponse> createMaterial(
            @PathVariable Long topicId,
            @Valid @RequestBody StudyMaterialRequest request,
            @AuthenticationPrincipal User user) {
        StudyMaterialResponse response = studyMaterialService.createMaterial(topicId, request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lista todos os materiais didaticos de um assunto.
     * GET /api/topics/{topicId}/materials
     */
    @GetMapping("/api/topics/{topicId}/materials")
    public ResponseEntity<List<StudyMaterialResponse>> getMaterialsByTopic(
            @PathVariable Long topicId,
            @AuthenticationPrincipal User user) {
        List<StudyMaterialResponse> materials = studyMaterialService.getMaterialsByTopic(topicId, user);
        return ResponseEntity.ok(materials);
    }

    /**
     * Retorna os detalhes de um material especifico.
     * GET /api/materials/{id}
     */
    @GetMapping("/api/materials/{id}")
    public ResponseEntity<StudyMaterialResponse> getMaterialById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        StudyMaterialResponse response = studyMaterialService.getMaterialById(id, user);
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza o conteudo ou titulo de um material existente.
     * PUT /api/materials/{id}
     */
    @PutMapping("/api/materials/{id}")
    public ResponseEntity<StudyMaterialResponse> updateMaterial(
            @PathVariable Long id,
            @Valid @RequestBody StudyMaterialRequest request,
            @AuthenticationPrincipal User user) {
        StudyMaterialResponse response = studyMaterialService.updateMaterial(id, request, user);
        return ResponseEntity.ok(response);
    }

    /**
     * Remove um material de estudo.
     * DELETE /api/materials/{id}
     */
    @DeleteMapping("/api/materials/{id}")
    public ResponseEntity<Void> deleteMaterial(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        studyMaterialService.deleteMaterial(id, user);
        return ResponseEntity.noContent().build();
    }
}
