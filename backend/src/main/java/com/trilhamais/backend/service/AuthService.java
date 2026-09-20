package com.trilhamais.backend.service;

import com.trilhamais.backend.config.JwtService;
import com.trilhamais.backend.dto.AuthResponse;
import com.trilhamais.backend.dto.LoginRequest;
import com.trilhamais.backend.dto.UserResponse;
import com.trilhamais.backend.exception.InvalidCredentialsException;
import com.trilhamais.backend.model.User;
import com.trilhamais.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servico responsavel pela orquestracao da autenticacao de estudantes:
 * validacao de credenciais com hash BCrypt e emissao de tokens JWT.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Realiza o login do usuario validando suas credenciais.
     */
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().toLowerCase().trim();

        // 1. Busca usuario pelo e-mail
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new InvalidCredentialsException("E-mail ou senha incorretos."));

        // 2. Valida a senha digitada contra o hash BCrypt salvo no banco
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("E-mail ou senha incorretos.");
        }

        // 3. Emite o token JWT
        String token = jwtService.generateToken(user);

        // 4. Monta DTO de resposta sem dados sensiveis
        UserResponse userResponse = new UserResponse(user.getId(), user.getName(), user.getEmail());
        long expiresInSeconds = jwtService.getExpirationMs() / 1000;

        return new AuthResponse(token, "Bearer", expiresInSeconds, userResponse);
    }
}
