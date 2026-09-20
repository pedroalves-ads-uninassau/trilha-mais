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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SubjectControllerTest {

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
    }

    @Test
    @DisplayName("Teste 1: Criacao de materia com token valido deve retornar 201 Created")
    void shouldCreateSubjectSuccessfully() throws Exception {
        String json = """
            {
              "name": "Banco de Dados",
              "description": "Modelagem relacional e SQL"
            }
            """;

        mockMvc.perform(post("/api/subjects")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Banco de Dados")))
                .andExpect(jsonPath("$.description", is("Modelagem relacional e SQL")))
                .andExpect(jsonPath("$.topicsCount", is(0)));

        assertEquals(1, subjectRepository.count());
    }

    @Test
    @DisplayName("Teste 2: Criacao de materia com nome vazio deve retornar 400 Bad Request")
    void shouldReturnBadRequestWhenNameIsBlank() throws Exception {
        String json = """
            {
              "name": "",
              "description": "Descricao qualquer"
            }
            """;

        mockMvc.perform(post("/api/subjects")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name", notNullValue()));
    }

    @Test
    @DisplayName("Teste 3: Criacao de materia duplicada para o mesmo usuario deve retornar 400 Bad Request")
    void shouldReturnBadRequestWhenNameIsDuplicate() throws Exception {
        Subject existing = new Subject("Matemática", "Estudos gerais", testUser);
        subjectRepository.save(existing);

        String json = """
            {
              "name": "Matemática",
              "description": "Outra tentativa"
            }
            """;

        mockMvc.perform(post("/api/subjects")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("já possui uma matéria cadastrada")));
    }

    @Test
    @DisplayName("Teste 4: Listagem deve retornar apenas materias do usuario autenticado (Isolamento Multitenancy)")
    void shouldListOnlySubjectsOfAuthenticatedUser() throws Exception {
        // Materia do usuario logado
        subjectRepository.save(new Subject("Matéria do Pedro", "Desc 1", testUser));

        // Outro usuario e sua materia
        User otherUser = new User("Outro Aluno", "outro@trilha.com", passwordEncoder.encode("senha123"));
        otherUser = userRepository.save(otherUser);
        subjectRepository.save(new Subject("Matéria do Outro", "Desc 2", otherUser));

        mockMvc.perform(get("/api/subjects")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Matéria do Pedro")));
    }

    @Test
    @DisplayName("Teste 5: Consulta de materia por ID deve retornar detalhes e lista de assuntos")
    void shouldGetSubjectDetailWithTopics() throws Exception {
        Subject subject = new Subject("Engenharia de Software", "Processos e qualidade", testUser);
        subject = subjectRepository.save(subject);

        Topic topic1 = new Topic("Scrum e Metodologias Ágeis", "Sprints e papéis", subject);
        Topic topic2 = new Topic("Testes de Software", "TDD e pirâmide de testes", subject);
        topicRepository.save(topic1);
        topicRepository.save(topic2);

        mockMvc.perform(get("/api/subjects/" + subject.getId())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(subject.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Engenharia de Software")))
                .andExpect(jsonPath("$.topics", hasSize(2)))
                .andExpect(jsonPath("$.topics[0].name", is("Scrum e Metodologias Ágeis")))
                .andExpect(jsonPath("$.topics[1].name", is("Testes de Software")));
    }

    @Test
    @DisplayName("Teste 6: Atualizacao de materia deve retornar 200 OK com novos dados")
    void shouldUpdateSubjectSuccessfully() throws Exception {
        Subject subject = subjectRepository.save(new Subject("Nome Antigo", "Desc Antiga", testUser));

        String updateJson = """
            {
              "name": "Nome Atualizado",
              "description": "Descricao Atualizada"
            }
            """;

        mockMvc.perform(put("/api/subjects/" + subject.getId())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Nome Atualizado")))
                .andExpect(jsonPath("$.description", is("Descricao Atualizada")));
    }

    @Test
    @DisplayName("Teste 7: Exclusao de materia deve retornar 204 No Content e remover topicos em cascata")
    void shouldDeleteSubjectAndCascadeTopics() throws Exception {
        Subject subject = subjectRepository.save(new Subject("Física", "Mecânica", testUser));
        topicRepository.save(new Topic("Cinemática", "Velocidade média", subject));

        mockMvc.perform(delete("/api/subjects/" + subject.getId())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNoContent());

        assertFalse(subjectRepository.existsById(subject.getId()));
        assertEquals(0, topicRepository.count());
    }

    @Test
    @DisplayName("Teste 8: Acesso a materias sem token Bearer deve retornar 401 Unauthorized")
    void shouldReturnUnauthorizedWithoutToken() throws Exception {
        mockMvc.perform(get("/api/subjects"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Teste 9: Tentativa de acessar materia de outro usuario deve retornar 404 Not Found")
    void shouldReturnNotFoundWhenAccessingSubjectOfAnotherUser() throws Exception {
        User otherUser = userRepository.save(new User("Carlos", "carlos@trilha.com", passwordEncoder.encode("senha123")));
        Subject otherSubject = subjectRepository.save(new Subject("Biologia", "Genética", otherUser));

        mockMvc.perform(get("/api/subjects/" + otherSubject.getId())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("não encontrada")));
    }
}
