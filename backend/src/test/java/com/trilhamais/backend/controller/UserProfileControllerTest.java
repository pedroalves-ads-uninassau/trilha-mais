package com.trilhamais.backend.controller;

import com.trilhamais.backend.config.JwtService;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

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

    @Autowired
    private JwtService jwtService;

    private User testUser;
    private String authToken;

    @BeforeEach
    void setUp() {
        scheduleRepository.deleteAll();
        quizAnswerRepository.deleteAll();
        quizQuestionRepository.deleteAll();
        quizRepository.deleteAll();
        userSettingsRepository.deleteAll();
        userRepository.deleteAll();

        testUser = userRepository.save(new User("Nome Antigo", "perfil@teste.com", passwordEncoder.encode("senha123")));
        authToken = jwtService.generateToken(testUser);
    }

    @Test
    @DisplayName("Deve editar nome e avatar do perfil (200)")
    void shouldUpdateProfile() throws Exception {
        mockMvc.perform(put("/api/users/me")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"  Nome Novo \",\"avatarEmoji\":\"🚀\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nome Novo"))
                .andExpect(jsonPath("$.avatarEmoji").value("🚀"));

        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nome Novo"))
                .andExpect(jsonPath("$.streakDays").value(0));
    }

    @Test
    @DisplayName("Deve recusar edição de perfil com nome vazio (400)")
    void shouldRejectBlankName() throws Exception {
        mockMvc.perform(put("/api/users/me")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve trocar a senha quando a senha atual está correta e recusar quando está errada")
    void shouldChangePassword() throws Exception {
        mockMvc.perform(put("/api/users/me/password")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"errada\",\"newPassword\":\"novaSenha123\"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/api/users/me/password")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"senha123\",\"newPassword\":\"novaSenha123\"}"))
                .andExpect(status().isOk());

        User reloaded = userRepository.findById(testUser.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches("novaSenha123", reloaded.getPassword()));
    }

    @Test
    @DisplayName("Deve devolver configurações padrão e salvar notificações e tema")
    void shouldGetAndUpdateSettings() throws Exception {
        mockMvc.perform(get("/api/users/me/settings")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notificationsEnabled").value(true))
                .andExpect(jsonPath("$.theme").value("light"));

        mockMvc.perform(put("/api/users/me/settings")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"notificationsEnabled\":false,\"theme\":\"DARK\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notificationsEnabled").value(false))
                .andExpect(jsonPath("$.theme").value("dark"));

        // Envio parcial mantém o que já estava salvo
        mockMvc.perform(put("/api/users/me/settings")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"notificationsEnabled\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notificationsEnabled").value(true))
                .andExpect(jsonPath("$.theme").value("dark"));
    }

    @Test
    @DisplayName("Deve recusar tema inválido (400)")
    void shouldRejectInvalidTheme() throws Exception {
        mockMvc.perform(put("/api/users/me/settings")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"theme\":\"roxo\"}"))
                .andExpect(status().isBadRequest());
    }
}
