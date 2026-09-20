package com.trilhamais.backend.controller;

import com.trilhamais.backend.dto.RegisterRequest;
import com.trilhamais.backend.dto.UserResponse;
import com.trilhamais.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * Endpoint protegido para obtencao do perfil do usuario autenticado.
     * GET /api/users/profile
     * Requer cabecalho Authorization: Bearer <token_jwt>
     */
    @GetMapping({"/profile", "/me"})
    public ResponseEntity<UserResponse> getProfile(@org.springframework.security.core.annotation.AuthenticationPrincipal com.trilhamais.backend.model.User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(new UserResponse(user.getId(), user.getName(), user.getEmail()));
    }
}

