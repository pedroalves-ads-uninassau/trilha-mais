package com.trilhamais.backend.controller;

import com.trilhamais.backend.dto.SubjectDetailResponse;
import com.trilhamais.backend.dto.SubjectRequest;
import com.trilhamais.backend.dto.SubjectResponse;
import com.trilhamais.backend.model.User;
import com.trilhamais.backend.service.SubjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gestao de Materias de estudo (Subject).
 * Todas as operacoes requerem autenticacao Bearer JWT e operam sobre dados do estudante logado.
 */
@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    /**
     * Cria uma nova materia de estudo para o estudante autenticado.
     * POST /api/subjects
     */
    @PostMapping
    public ResponseEntity<SubjectResponse> createSubject(
            @Valid @RequestBody SubjectRequest request,
            @AuthenticationPrincipal User user) {
        SubjectResponse response = subjectService.createSubject(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lista todas as materias do estudante autenticado com contagem de assuntos.
     * GET /api/subjects
     */
    @GetMapping
    public ResponseEntity<List<SubjectResponse>> getAllSubjects(@AuthenticationPrincipal User user) {
        List<SubjectResponse> subjects = subjectService.getAllSubjects(user);
        return ResponseEntity.ok(subjects);
    }

    /**
     * Retorna os detalhes de uma materia especifica, incluindo a lista de assuntos.
     * GET /api/subjects/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<SubjectDetailResponse> getSubjectById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        SubjectDetailResponse response = subjectService.getSubjectById(id, user);
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza dados de uma materia existente.
     * PUT /api/subjects/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<SubjectResponse> updateSubject(
            @PathVariable Long id,
            @Valid @RequestBody SubjectRequest request,
            @AuthenticationPrincipal User user) {
        SubjectResponse response = subjectService.updateSubject(id, request, user);
        return ResponseEntity.ok(response);
    }

    /**
     * Remove uma materia e seus respectivos assuntos associados.
     * DELETE /api/subjects/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        subjectService.deleteSubject(id, user);
        return ResponseEntity.noContent().build();
    }
}
