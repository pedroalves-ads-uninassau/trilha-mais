package com.trilhamais.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trilhamais.backend.config.JwtService;
import com.trilhamais.backend.dto.AnswerItemRequest;
import com.trilhamais.backend.dto.QuizGenerateRequest;
import com.trilhamais.backend.dto.QuizSubmitRequest;
import com.trilhamais.backend.model.*;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class QuizControllerTest {

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
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionOptionRepository questionOptionRepository;

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

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User testUser;
    private String authToken;
    private Subject testSubject;
    private Topic testTopic;
    private Question q1;
    private Question q2;

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

        testUser = new User();
        testUser.setName("Pedro Aluno");
        testUser.setEmail("pedro.simulado@teste.com");
        testUser.setPassword(passwordEncoder.encode("senha123"));
        testUser = userRepository.save(testUser);

        authToken = jwtService.generateToken(testUser);

        testSubject = new Subject("Banco de Dados", "Modelagem e SQL", testUser);
        testSubject = subjectRepository.save(testSubject);

        testTopic = new Topic("SQL Básico", "Comandos DDL e DML", testSubject);
        testTopic = topicRepository.save(testTopic);

        // Cria questões com opções para o tópico
        q1 = new Question("Qual comando SQL é utilizado para extrair dados de uma tabela?",
                "O comando SELECT é a instrução DQL usada para consultar dados.", testTopic);
        q1.addOption(new QuestionOption("SELECT", true));
        q1.addOption(new QuestionOption("UPDATE", false));
        q1.addOption(new QuestionOption("DELETE", false));
        q1.addOption(new QuestionOption("DROP", false));
        q1 = questionRepository.save(q1);

        q2 = new Question("Qual comando SQL remove uma tabela inteira do banco de dados?",
                "DROP TABLE remove a definição e todos os registros da tabela.", testTopic);
        q2.addOption(new QuestionOption("DROP TABLE", true));
        q2.addOption(new QuestionOption("TRUNCATE TABLE", false));
        q2.addOption(new QuestionOption("REMOVE TABLE", false));
        q2.addOption(new QuestionOption("ALTER TABLE", false));
        q2 = questionRepository.save(q2);
    }

    @Test
    @DisplayName("Deve gerar um simulado dinâmico com sucesso (201)")
    void shouldGenerateQuizSuccessfully() throws Exception {
        QuizGenerateRequest request = new QuizGenerateRequest(testTopic.getId(), null, 10, "Simulado de SQL");

        mockMvc.perform(post("/api/quizzes/generate")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("Simulado de SQL"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.totalQuestions").value(2))
                .andExpect(jsonPath("$.questions", hasSize(2)));
    }

    @Test
    @DisplayName("Anti-Fraude: Deve ocultar as alternativas corretas durante a execução do simulado")
    void shouldHideCorrectOptionDuringQuizExecution() throws Exception {
        QuizGenerateRequest request = new QuizGenerateRequest(testTopic.getId(), null, 10, "Simulado Sigiloso");

        String responseStr = mockMvc.perform(post("/api/quizzes/generate")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long quizId = objectMapper.readTree(responseStr).get("id").asLong();

        // Consulta a prova
        String quizDetailsStr = mockMvc.perform(get("/api/quizzes/" + quizId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andReturn().getResponse().getContentAsString();

        // Confirma que 'isCorrect' não foi serializado no payload
        assertFalse(quizDetailsStr.contains("\"isCorrect\""), "O campo 'isCorrect' não deve ser retornado durante o teste");
        assertFalse(quizDetailsStr.contains("\"explanation\""), "A explicação não deve ser revelada antes da submissão");
    }

    @Test
    @DisplayName("Deve submeter respostas, calcular nota 10.0 para 100% de acertos e retornar gabarito comentado")
    void shouldSubmitQuizAndCalculateScore() throws Exception {
        QuizGenerateRequest request = new QuizGenerateRequest(testTopic.getId(), null, 2, "Simulado Avaliativo");

        String generateStr = mockMvc.perform(post("/api/quizzes/generate")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JsonNode genJson = objectMapper.readTree(generateStr);
        Long quizId = genJson.get("id").asLong();

        // Identifica as opções corretas de q1 e q2
        Long correctOptQ1 = q1.getOptions().stream().filter(QuestionOption::isCorrect).findFirst().get().getId();
        Long correctOptQ2 = q2.getOptions().stream().filter(QuestionOption::isCorrect).findFirst().get().getId();

        QuizSubmitRequest submitRequest = new QuizSubmitRequest(List.of(
                new AnswerItemRequest(q1.getId(), correctOptQ1),
                new AnswerItemRequest(q2.getId(), correctOptQ2)
        ));

        mockMvc.perform(post("/api/quizzes/" + quizId + "/submit")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quizId").value(quizId))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.totalQuestions").value(2))
                .andExpect(jsonPath("$.correctAnswers").value(2))
                .andExpect(jsonPath("$.score").value(10.0))
                .andExpect(jsonPath("$.percentage").value(100.0))
                .andExpect(jsonPath("$.results", hasSize(2)))
                .andExpect(jsonPath("$.results[0].isCorrect").value(true))
                .andExpect(jsonPath("$.results[0].explanation").isNotEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar re-submissão de simulado já finalizado (400)")
    void shouldRejectSecondSubmission() throws Exception {
        QuizGenerateRequest request = new QuizGenerateRequest(testTopic.getId(), null, 2, "Simulado Único");

        String generateStr = mockMvc.perform(post("/api/quizzes/generate")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long quizId = objectMapper.readTree(generateStr).get("id").asLong();
        Long correctOptQ1 = q1.getOptions().stream().filter(QuestionOption::isCorrect).findFirst().get().getId();

        QuizSubmitRequest submitRequest = new QuizSubmitRequest(List.of(
                new AnswerItemRequest(q1.getId(), correctOptQ1)
        ));

        // Primeira submissão - Sucesso
        mockMvc.perform(post("/api/quizzes/" + quizId + "/submit")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitRequest)))
                .andExpect(status().isOk());

        // Segunda submissão - Rejeição 400
        mockMvc.perform(post("/api/quizzes/" + quizId + "/submit")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("já foi finalizado")));
    }

    @Test
    @DisplayName("Deve consultar o resultado e gabarito comentado de um simulado concluído (200)")
    void shouldGetCompletedQuizResult() throws Exception {
        QuizGenerateRequest request = new QuizGenerateRequest(testTopic.getId(), null, 2, "Simulado Teste");

        String generateStr = mockMvc.perform(post("/api/quizzes/generate")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long quizId = objectMapper.readTree(generateStr).get("id").asLong();
        Long correctOptQ1 = q1.getOptions().stream().filter(QuestionOption::isCorrect).findFirst().get().getId();

        mockMvc.perform(post("/api/quizzes/" + quizId + "/submit")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new QuizSubmitRequest(List.of(
                                new AnswerItemRequest(q1.getId(), correctOptQ1)
                        )))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/quizzes/" + quizId + "/result")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quizId").value(quizId))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    @DisplayName("Deve rejeitar visualização de resultado se o simulado ainda estiver em andamento (400)")
    void shouldRejectGetResultIfQuizInProgress() throws Exception {
        QuizGenerateRequest request = new QuizGenerateRequest(testTopic.getId(), null, 2, "Simulado Incompleto");

        String generateStr = mockMvc.perform(post("/api/quizzes/generate")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long quizId = objectMapper.readTree(generateStr).get("id").asLong();

        mockMvc.perform(get("/api/quizzes/" + quizId + "/result")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("ainda não foi concluído")));
    }

    @Test
    @DisplayName("Deve listar histórico de simulados do usuário autenticado (200)")
    void shouldListUserQuizzes() throws Exception {
        QuizGenerateRequest r1 = new QuizGenerateRequest(testTopic.getId(), null, 2, "Simulado 1");
        QuizGenerateRequest r2 = new QuizGenerateRequest(testTopic.getId(), null, 2, "Simulado 2");

        mockMvc.perform(post("/api/quizzes/generate")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(r1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/quizzes/generate")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(r2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/quizzes")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("Isolamento Multitenancy: Usuário B não pode acessar simulado do Usuário A (404)")
    void shouldIsolateQuizzesBetweenUsers() throws Exception {
        QuizGenerateRequest request = new QuizGenerateRequest(testTopic.getId(), null, 2, "Simulado Privado A");

        String generateStr = mockMvc.perform(post("/api/quizzes/generate")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long quizId = objectMapper.readTree(generateStr).get("id").asLong();

        // Aluno B
        User userB = new User();
        userB.setName("Estudante Invasor");
        userB.setEmail("invasor@teste.com");
        userB.setPassword(passwordEncoder.encode("senha123"));
        userB = userRepository.save(userB);
        String tokenB = jwtService.generateToken(userB);

        mockMvc.perform(get("/api/quizzes/" + quizId)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve gerar simulado para uma matéria inteira com questionCount = 15")
    void shouldGenerateQuizForSubjectWithFifteenQuestions() throws Exception {
        QuizGenerateRequest request = new QuizGenerateRequest(null, testSubject.getId(), 15, "Simulado Geral 15");

        mockMvc.perform(post("/api/quizzes/generate")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("Simulado Geral 15"))
                .andExpect(jsonPath("$.subjectId").value(testSubject.getId()));
    }
}
