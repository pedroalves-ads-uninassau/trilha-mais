package com.trilhamais.backend.controller;

import com.trilhamais.backend.dto.AuthResponse;
import com.trilhamais.backend.dto.ForgotPasswordRequest;
import com.trilhamais.backend.dto.LoginRequest;
import com.trilhamais.backend.dto.MessageResponse;
import com.trilhamais.backend.dto.ResetPasswordRequest;
import com.trilhamais.backend.service.AuthService;
import com.trilhamais.backend.service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST responsavel pelas operacoes de autenticacao (Login e recuperacao de senha).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    public AuthController(AuthService authService, PasswordResetService passwordResetService) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
    }

    /**
     * Endpoint publico para autenticacao de estudantes.
     * Retorna token JWT de acesso quando as credenciais estao corretas.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint publico que envia um codigo de 6 digitos para o e-mail do estudante.
     * Sempre responde a mesma mensagem, para nao revelar quais e-mails estao cadastrados.
     * POST /api/auth/forgot-password
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.requestReset(request);
        return ResponseEntity.ok(new MessageResponse(
                "Se o e-mail estiver cadastrado, você receberá um código para criar uma nova senha."));
    }

    /**
     * Endpoint publico que troca a senha usando o codigo recebido por e-mail.
     * POST /api/auth/reset-password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok(new MessageResponse("Senha alterada com sucesso. Faça login com a nova senha."));
    }
}
