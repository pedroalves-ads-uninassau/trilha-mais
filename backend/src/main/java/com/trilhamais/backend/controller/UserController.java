package com.trilhamais.backend.controller;

import com.trilhamais.backend.dto.ChangePasswordRequest;
import com.trilhamais.backend.dto.MessageResponse;
import com.trilhamais.backend.dto.RegisterRequest;
import com.trilhamais.backend.dto.UpdateProfileRequest;
import com.trilhamais.backend.dto.UserResponse;
import com.trilhamais.backend.dto.UserSettingsDto;
import com.trilhamais.backend.model.User;
import com.trilhamais.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST responsavel pelas operacoes relacionadas a usuarios.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint para cadastro de um novo usuario.
     * POST /api/users/register
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint protegido para obtencao do perfil do usuario autenticado com contadores de estudos.
     * GET /api/users/profile
     * Requer cabecalho Authorization: Bearer <token_jwt>
     */
    @GetMapping({"/profile", "/me"})
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(userService.getProfile(user));
    }

    /**
     * Edita nome e avatar do estudante.
     * PUT /api/users/me
     */
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(user, request));
    }

    /**
     * Troca a senha do estudante logado (exige a senha atual).
     * PUT /api/users/me/password
     */
    @PutMapping("/me/password")
    public ResponseEntity<MessageResponse> changePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(user, request);
        return ResponseEntity.ok(new MessageResponse("Senha alterada com sucesso."));
    }

    /**
     * Configuracoes do Perfil (Notificacoes e Tema).
     * GET /api/users/me/settings
     */
    @GetMapping("/me/settings")
    public ResponseEntity<UserSettingsDto> getSettings(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getSettings(user));
    }

    /**
     * Atualiza as configuracoes do Perfil.
     * PUT /api/users/me/settings
     */
    @PutMapping("/me/settings")
    public ResponseEntity<UserSettingsDto> updateSettings(
            @AuthenticationPrincipal User user,
            @RequestBody UserSettingsDto request) {
        return ResponseEntity.ok(userService.updateSettings(user, request));
    }
}
