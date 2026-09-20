package com.trilhamais.backend.controller;

import com.trilhamais.backend.config.JwtService;
import com.trilhamais.backend.model.User;
import com.trilhamais.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.trilhamais.backend.repository.SubjectRepository subjectRepository;

    @Autowired
    private com.trilhamais.backend.repository.StudyMaterialRepository studyMaterialRepository;

    @Autowired
    private com.trilhamais.backend.repository.QuestionOptionRepository questionOptionRepository;

    @Autowired
    private com.trilhamais.backend.repository.QuestionRepository questionRepository;

    @Autowired
    private com.trilhamais.backend.repository.QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private com.trilhamais.backend.repository.QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private com.trilhamais.backend.repository.QuizRepository quizRepository;

    @Autowired
    private com.trilhamais.backend.repository.TopicRepository topicRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        quizAnswerRepository.deleteAll();
        quizQuestionRepository.deleteAll();
        quizRepository.deleteAll();
        questionOptionRepository.deleteAll();
        questionRepository.deleteAll();
        studyMaterialRepository.deleteAll();
        topicRepository.deleteAll();
        subjectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Teste 1: Login com credenciais corretas deve retornar 200 OK, token JWT Bearer e dados do usuario")
    void shouldLoginSuccessfullyAndReturnJwtToken() throws Exception {
        // Cria usuario previo com senha criptografada via BCrypt
        User user = new User("Pedro Aluno", "pedro@trilha.com", passwordEncoder.encode("senha12345"));
        userRepository.save(user);

        String loginJson = """
            {
              "email": "pedro@trilha.com",
              "password": "senha12345"
            }
            """;

        String responseBody = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", notNullValue()))
                .andExpect(jsonPath("$.tokenType", is("Bearer")))
                .andExpect(jsonPath("$.expiresIn", greaterThan(0)))
                .andExpect(jsonPath("$.user.id", notNullValue()))
                .andExpect(jsonPath("$.user.name", is("Pedro Aluno")))
                .andExpect(jsonPath("$.user.email", is("pedro@trilha.com")))
                .andExpect(jsonPath("$.user.password").doesNotExist())
                .andReturn().getResponse().getContentAsString();

        // Extrai o token e valida a integridade com o JwtService
        String token = responseBody.split("\"accessToken\":\"")[1].split("\"")[0];
        assertTrue(jwtService.isTokenValid(token));
        assertEquals("pedro@trilha.com", jwtService.extractEmail(token));
    }

    @Test
    @DisplayName("Teste 2: Login com senha incorreta deve retornar 401 Unauthorized")
    void shouldReturnUnauthorizedWhenPasswordIsIncorrect() throws Exception {
        User user = new User("Pedro Aluno", "pedro@trilha.com", passwordEncoder.encode("senhaCorreta123"));
        userRepository.save(user);

        String wrongPasswordJson = """
            {
              "email": "pedro@trilha.com",
              "password": "senhaErrada999"
            }
            """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(wrongPasswordJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("E-mail ou senha incorretos.")));
    }

    @Test
    @DisplayName("Teste 3: Login com e-mail inexistente deve retornar 401 Unauthorized")
    void shouldReturnUnauthorizedWhenEmailDoesNotExist() throws Exception {
        String nonExistentUserJson = """
            {
              "email": "naoexiste@trilha.com",
              "password": "qualquersenha"
            }
            """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nonExistentUserJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("E-mail ou senha incorretos.")));
    }

    @Test
    @DisplayName("Teste 4: Login com campos vazios ou invalidos deve retornar 400 Bad Request")
    void shouldReturnBadRequestWhenFieldsAreBlank() throws Exception {
        String blankFieldsJson = """
            {
              "email": "",
              "password": ""
            }
            """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(blankFieldsJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Dados inválidos.")))
                .andExpect(jsonPath("$.errors.email", notNullValue()))
                .andExpect(jsonPath("$.errors.password", notNullValue()));
    }

    @Test
    @DisplayName("Teste 5: Acesso ao perfil com Token Bearer valido deve retornar 200 OK e dados do estudante")
    void shouldAccessProfileSuccessfullyWhenTokenProvided() throws Exception {
        User user = new User("Maria Silva", "maria@trilha.com", passwordEncoder.encode("maria1234"));
        user = userRepository.save(user);

        String token = jwtService.generateToken(user);

        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Maria Silva")))
                .andExpect(jsonPath("$.email", is("maria@trilha.com")))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("Teste 6: Acesso ao perfil sem token de autenticacao deve retornar 401 Unauthorized")
    void shouldReturnUnauthorizedWhenAccessingProfileWithoutToken() throws Exception {
        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", containsString("Não autorizado")));
    }

    @Test
    @DisplayName("Teste 7: Acesso ao perfil com token invalido/falso deve retornar 401 Unauthorized")
    void shouldReturnUnauthorizedWhenAccessingProfileWithInvalidToken() throws Exception {
        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer token_falsificado_invalido_123"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", containsString("Não autorizado")));
    }
}
