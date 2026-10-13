package com.trilhamais.backend.controller;

import com.trilhamais.backend.dto.ScheduleRequest;
import com.trilhamais.backend.dto.ScheduleResponse;
import com.trilhamais.backend.model.User;
import com.trilhamais.backend.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    /**
     * Lista todos os compromissos agendados de estudo do estudante autenticado.
     * GET /api/schedules
     */
    @GetMapping
    public ResponseEntity<List<ScheduleResponse>> getSchedules(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(scheduleService.getSchedulesByUser(user));
    }

    /**
     * Adiciona um novo compromisso na Agenda.
     * POST /api/schedules
     */
    @PostMapping
    public ResponseEntity<ScheduleResponse> createSchedule(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleService.createSchedule(user, request));
    }

    /**
     * Alterna o estado de conclusão (pendente/concluído) de um compromisso da agenda.
     * PATCH /api/schedules/{id}/toggle
     */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ScheduleResponse> toggleCompleted(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.toggleCompleted(user, id));
    }

    /**
     * Remove um compromisso da agenda.
     * DELETE /api/schedules/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        scheduleService.deleteSchedule(user, id);
        return ResponseEntity.noContent().build();
    }
}
