package com.trilhamais.backend.controller;

import com.trilhamais.backend.config.JwtService;
import com.trilhamais.backend.model.Subject;
import com.trilhamais.backend.model.Topic;
import com.trilhamais.backend.model.User;
import com.trilhamais.backend.repository.SubjectRepository;
import com.trilhamais.backend.repository.TopicRepository;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private TopicRepository topicRepository;

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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private User testUser;
    private String authToken;
    private Subject testSubject;

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

        testUser = new User("Pedro Aluno", "pedro@trilha.com", passwordEncoder.encode("senha123"));
        testUser = userRepository.save(testUser);
        authToken = jwtService.generateToken(testUser);

        testSubject = new Subject("Programação Orientada a Objetos", "Java e design patterns", testUser);
        testSubject = subjectRepository.save(testSubject);
    }

    @Test
    @DisplayName("Teste 1: Criacao de assunto vinculado a materia deve retornar 201 Created")
    void shouldCreateTopicSuccessfully() throws Exception {
        String json = """
            {
              "name": "Polimorfismo e Herança",
              "description": "Conceitos fundamentais de OOP"
            }
            """;

        mockMvc.perform(post("/api/subjects/" + testSubject.getId() + "/topics")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Polimorfismo e Herança")))
                .andExpect(jsonPath("$.subjectId", is(testSubject.getId().intValue())))
                .andExpect(jsonPath("$.subjectName", is("Programação Orientada a Objetos")));
    }

    @Test
    @DisplayName("Teste 2: Criacao de assunto com nome vazio deve retornar 400 Bad Request")
    void shouldReturnBadRequestWhenTopicNameIsBlank() throws Exception {
        String json = """
            {
              "name": "",
              "description": "Sem nome"
            }
            """;

        mockMvc.perform(post("/api/subjects/" + testSubject.getId() + "/topics")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name", notNullValue()));
    }

    @Test
    @DisplayName("Teste 3: Listagem de assuntos de uma materia deve retornar 200 OK")
    void shouldListTopicsOfSubject() throws Exception {
        topicRepository.save(new Topic("Interfaces", "Contratos", testSubject));
        topicRepository.save(new Topic("Encapsulamento", "Modificadores de acesso", testSubject));

        mockMvc.perform(get("/api/subjects/" + testSubject.getId() + "/topics")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Encapsulamento")))
                .andExpect(jsonPath("$[1].name", is("Interfaces")));
    }

    @Test
    @DisplayName("Teste 4: Consulta de assunto por ID deve retornar 200 OK")
    void shouldGetTopicById() throws Exception {
        Topic topic = topicRepository.save(new Topic("Classes Abstratas", "Templates de classes", testSubject));

        mockMvc.perform(get("/api/topics/" + topic.getId())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(topic.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Classes Abstratas")));
    }

    @Test
    @DisplayName("Teste 5: Atualizacao de assunto deve retornar 200 OK")
    void shouldUpdateTopicSuccessfully() throws Exception {
        Topic topic = topicRepository.save(new Topic("Nome Antigo", "Desc Antiga", testSubject));

        String updateJson = """
            {
              "name": "Design Patterns - Factory",
              "description": "Padrão de criação"
            }
            """;

        mockMvc.perform(put("/api/topics/" + topic.getId())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Design Patterns - Factory")))
                .andExpect(jsonPath("$.description", is("Padrão de criação")));
    }

    @Test
    @DisplayName("Teste 6: Exclusao de assunto deve retornar 204 No Content e manter a materia intacta")
    void shouldDeleteTopicSuccessfully() throws Exception {
        Topic topic = topicRepository.save(new Topic("Tópico para deletar", "Desc", testSubject));

        mockMvc.perform(delete("/api/topics/" + topic.getId())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNoContent());

        assertFalse(topicRepository.existsById(topic.getId()));
        assertTrue(subjectRepository.existsById(testSubject.getId()));
    }

    @Test
    @DisplayName("Teste 7: Tentativa de adicionar assunto a materia de outro usuario deve retornar 404 Not Found")
    void shouldReturnNotFoundWhenSubjectBelongsToAnotherUser() throws Exception {
        User otherUser = userRepository.save(new User("Aluno 2", "aluno2@trilha.com", passwordEncoder.encode("senha123")));
        Subject otherSubject = subjectRepository.save(new Subject("Matéria Alheia", "Desc", otherUser));

        String json = """
            {
              "name": "Tentativa Invalida",
              "description": "Desc"
            }
            """;

        mockMvc.perform(post("/api/subjects/" + otherSubject.getId() + "/topics")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("não encontrada")));
    }
}
