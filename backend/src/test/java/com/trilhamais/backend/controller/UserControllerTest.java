package com.trilhamais.backend.controller;

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

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

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
    @DisplayName("Teste 1: Cadastro valido deve retornar 201 Created, salvar usuario, nao expor senha e gerar hash BCrypt")
    void shouldRegisterUserSuccessfully() throws Exception {
        String json = """
            {
              "name": "Pedro",
              "email": "pedro@email.com",
              "password": "12345678"
            }
            """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Pedro")))
                .andExpect(jsonPath("$.email", is("pedro@email.com")))
                .andExpect(jsonPath("$.password").doesNotExist()); // Senha nunca deve ser exposta

        // Verifica no banco de dados MySQL
        Optional<User> savedUser = userRepository.findByEmail("pedro@email.com");
        assertTrue(savedUser.isPresent(), "O usuario deve estar persistido no banco de dados");
        assertEquals("Pedro", savedUser.get().getName());

        // Verifica se a senha armazenada esta no formato BCrypt e corresponde a senha digitada
        assertTrue(savedUser.get().getPassword().startsWith("$2a$") || savedUser.get().getPassword().startsWith("$2b$"));
        assertTrue(passwordEncoder.matches("12345678", savedUser.get().getPassword()));
    }

    @Test
    @DisplayName("Teste 2: Tentativa de cadastrar e-mail duplicado deve retornar 409 Conflict")
    void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        // Primeiro cadastro
        String firstUserJson = """
            {
              "name": "Pedro",
              "email": "pedro@email.com",
              "password": "12345678"
            }
            """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstUserJson))
                .andExpect(status().isCreated());

        // Tentativa de duplicacao
        String duplicateUserJson = """
            {
              "name": "Pedro Outro",
              "email": "pedro@email.com",
              "password": "outrasenha123"
            }
            """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateUserJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("E-mail já cadastrado.")));

        // Garante que apenas 1 usuario com esse e-mail existe no banco
        assertEquals(1, userRepository.count());
    }

    @Test
    @DisplayName("Teste 3: E-mail invalido deve retornar 400 Bad Request")
    void shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {
        String invalidEmailJson = """
            {
              "name": "Pedro",
              "email": "email-invalido",
              "password": "12345678"
            }
            """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEmailJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Dados inválidos.")))
                .andExpect(jsonPath("$.errors.email", notNullValue()));

        assertEquals(0, userRepository.count());
    }

    @Test
    @DisplayName("Teste 4: Campos obrigatorios vazios devem retornar 400 Bad Request")
    void shouldReturnBadRequestWhenFieldsAreBlank() throws Exception {
        String blankFieldsJson = """
            {
              "name": "",
              "email": "",
              "password": ""
            }
            """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(blankFieldsJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Dados inválidos.")))
                .andExpect(jsonPath("$.errors.name", notNullValue()))
                .andExpect(jsonPath("$.errors.email", notNullValue()))
                .andExpect(jsonPath("$.errors.password", notNullValue()));

        assertEquals(0, userRepository.count());
    }

    @Test
    @DisplayName("Teste 5: Senha armazenada no banco NAO deve ser igual a senha original")
    void shouldVerifyPasswordIsNotStoredAsPlainText() throws Exception {
        String plainPassword = "MinhaSenhaSuperSecreta123";
        String userJson = """
            {
              "name": "Carlos",
              "email": "carlos@email.com",
              "password": "%s"
            }
            """.formatted(plainPassword);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated());

        User user = userRepository.findByEmail("carlos@email.com").orElseThrow();
        assertNotEquals(plainPassword, user.getPassword(), "A senha no banco nunca deve ser o texto puro");
        assertTrue(passwordEncoder.matches(plainPassword, user.getPassword()), "O hash deve ser validado pelo PasswordEncoder");
    }
}
