package com.trilhamais.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trilhamais.backend.dto.ForgotPasswordRequest;
import com.trilhamais.backend.dto.LoginRequest;
import com.trilhamais.backend.dto.ResetPasswordRequest;
import com.trilhamais.backend.model.PasswordResetToken;
import com.trilhamais.backend.model.User;
import com.trilhamais.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PasswordResetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        scheduleRepository.deleteAll();
        quizAnswerRepository.deleteAll();
        quizQuestionRepository.deleteAll();
        quizRepository.deleteAll();
        tokenRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User("Aluno Esquecido", "esquecido@teste.com", passwordEncoder.encode("senhaAntiga"));
        testUser = userRepository.save(testUser);
    }

    @Test
    @DisplayName("Deve gerar código de recuperação (só o hash é salvo) para e-mail cadastrado (200)")
    void shouldCreateResetTokenForRegisteredEmail() throws Exception {
        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ForgotPasswordRequest("Esquecido@Teste.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        assertEquals(1, tokenRepository.count());
        PasswordResetToken token = tokenRepository.findAll().get(0);
        assertEquals(64, token.getTokenHash().length());
        assertTrue(token.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    @Test
    @DisplayName("Não deve revelar se o e-mail existe: mesma resposta 200 e nenhum código criado")
    void shouldNotRevealUnknownEmail() throws Exception {
        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ForgotPasswordRequest("ninguem@teste.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        assertEquals(0, tokenRepository.count());
    }

    @Test
    @DisplayName("Deve trocar a senha com código válido e permitir login com a nova senha")
    void shouldResetPasswordWithValidCode() throws Exception {
        tokenRepository.save(new PasswordResetToken(testUser, sha256("123456"), LocalDateTime.now().plusMinutes(30)));

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ResetPasswordRequest("esquecido@teste.com", "123456", "novaSenha123"))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("esquecido@teste.com", "novaSenha123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());

        // O código não pode ser usado de novo
        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ResetPasswordRequest("esquecido@teste.com", "123456", "outraSenha123"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve recusar código errado (400), contar a tentativa e manter a senha antiga")
    void shouldRejectWrongCode() throws Exception {
        tokenRepository.save(new PasswordResetToken(testUser, sha256("123456"), LocalDateTime.now().plusMinutes(30)));

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ResetPasswordRequest("esquecido@teste.com", "000000", "novaSenha123"))))
                .andExpect(status().isBadRequest());

        assertEquals(1, tokenRepository.findAll().get(0).getAttempts());
        User reloaded = userRepository.findById(testUser.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches("senhaAntiga", reloaded.getPassword()));
    }

    @Test
    @DisplayName("Deve recusar código expirado (400)")
    void shouldRejectExpiredCode() throws Exception {
        tokenRepository.save(new PasswordResetToken(testUser, sha256("123456"), LocalDateTime.now().minusMinutes(1)));

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ResetPasswordRequest("esquecido@teste.com", "123456", "novaSenha123"))))
                .andExpect(status().isBadRequest());
    }

    private static String sha256(String value) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
    }
}
