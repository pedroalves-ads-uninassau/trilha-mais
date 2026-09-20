package com.trilhamais.backend.controller;

import com.trilhamais.backend.config.JwtService;
import com.trilhamais.backend.model.MaterialType;
import com.trilhamais.backend.model.StudyMaterial;
import com.trilhamais.backend.model.Subject;
import com.trilhamais.backend.model.Topic;
import com.trilhamais.backend.model.User;
import com.trilhamais.backend.repository.StudyMaterialRepository;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StudyMaterialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private StudyMaterialRepository studyMaterialRepository;

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
    private Topic testTopic;

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

        Subject subject = subjectRepository.save(new Subject("Estruturas de Dados", "Árvores e Grafos", testUser));
        testTopic = topicRepository.save(new Topic("Árvores Binárias de Busca", "Propriedades e buscas", subject));
    }

    @Test
    @DisplayName("Teste 1: Criacao de material de estudo com token deve retornar 201 Created")
    void shouldCreateStudyMaterialSuccessfully() throws Exception {
        String json = """
            {
              "title": "Resumo de BST",
              "content": "Em uma BST, todo filho a esquerda e menor e a direita e maior.",
              "type": "SUMMARY"
            }
            """;

        mockMvc.perform(post("/api/topics/" + testTopic.getId() + "/materials")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Resumo de BST")))
                .andExpect(jsonPath("$.type", is("SUMMARY")))
                .andExpect(jsonPath("$.topicId", is(testTopic.getId().intValue())))
                .andExpect(jsonPath("$.topicName", is("Árvores Binárias de Busca")));

        assertEquals(1, studyMaterialRepository.count());
    }

    @Test
    @DisplayName("Teste 2: Criacao com titulo ou conteudo vazios deve retornar 400 Bad Request")
    void shouldReturnBadRequestWhenFieldsBlank() throws Exception {
        String json = """
            {
              "title": "",
              "content": ""
            }
            """;

        mockMvc.perform(post("/api/topics/" + testTopic.getId() + "/materials")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title", notNullValue()))
                .andExpect(jsonPath("$.errors.content", notNullValue()));
    }

    @Test
    @DisplayName("Teste 3: Listagem de materiais de um assunto deve retornar 200 OK")
    void shouldListMaterialsOfTopic() throws Exception {
        studyMaterialRepository.save(new StudyMaterial("Nota 1", "Conteudo 1", MaterialType.NOTES, testTopic));
        studyMaterialRepository.save(new StudyMaterial("Nota 2", "Conteudo 2", MaterialType.TEXT, testTopic));

        mockMvc.perform(get("/api/topics/" + testTopic.getId() + "/materials")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("Teste 4: Consulta de material por ID deve retornar 200 OK")
    void shouldGetMaterialById() throws Exception {
        StudyMaterial material = studyMaterialRepository.save(new StudyMaterial("Anotacao de Aula", "Conteudo explicativo", MaterialType.NOTES, testTopic));

        mockMvc.perform(get("/api/materials/" + material.getId())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(material.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Anotacao de Aula")));
    }

    @Test
    @DisplayName("Teste 5: Atualizacao de material deve retornar 200 OK com dados alterados")
    void shouldUpdateMaterialSuccessfully() throws Exception {
        StudyMaterial material = studyMaterialRepository.save(new StudyMaterial("Titulo Antigo", "Texto Antigo", MaterialType.NOTES, testTopic));

        String updateJson = """
            {
              "title": "Titulo Atualizado",
              "content": "Texto Atualizado e expandido",
              "type": "SUMMARY"
            }
            """;

        mockMvc.perform(put("/api/materials/" + material.getId())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Titulo Atualizado")))
                .andExpect(jsonPath("$.type", is("SUMMARY")));
    }

    @Test
    @DisplayName("Teste 6: Exclusao de material deve retornar 204 No Content")
    void shouldDeleteMaterialSuccessfully() throws Exception {
        StudyMaterial material = studyMaterialRepository.save(new StudyMaterial("Para Excluir", "Conteudo", MaterialType.NOTES, testTopic));

        mockMvc.perform(delete("/api/materials/" + material.getId())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNoContent());

        assertFalse(studyMaterialRepository.existsById(material.getId()));
    }

    @Test
    @DisplayName("Teste 7: Tentativa de cadastrar material em assunto de outro usuario deve retornar 404 Not Found")
    void shouldReturnNotFoundWhenTopicBelongsToOtherUser() throws Exception {
        User otherUser = userRepository.save(new User("Outro", "outro@trilha.com", passwordEncoder.encode("senha123")));
        Subject otherSubject = subjectRepository.save(new Subject("Outra Matéria", "Desc", otherUser));
        Topic otherTopic = topicRepository.save(new Topic("Tópico Alheio", "Desc", otherSubject));

        String json = """
            {
              "title": "Tentativa Invalida",
              "content": "Conteudo teste"
            }
            """;

        mockMvc.perform(post("/api/topics/" + otherTopic.getId() + "/materials")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("não encontrado")));
    }

    @Test
    @DisplayName("Teste 8: Acesso a materiais sem token Bearer deve retornar 401 Unauthorized")
    void shouldReturnUnauthorizedWithoutToken() throws Exception {
        mockMvc.perform(get("/api/topics/" + testTopic.getId() + "/materials"))
                .andExpect(status().isUnauthorized());
    }
}
