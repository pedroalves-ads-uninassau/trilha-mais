package com.trilhamais.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trilhamais.backend.config.JwtService;
import com.trilhamais.backend.model.*;
import com.trilhamais.backend.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes da rotina de estudos: agenda ligada a materia/assunto, sequencia de dias (streak),
 * visao geral da tela Progresso, historico do chat com IA e ids do "Continuar de onde parou".
 */
@SpringBootTest(properties = "groq.api.key=")
@AutoConfigureMockMvc
class StudyRoutineControllerTest {

    private static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");

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
    private ScheduleRepository scheduleRepository;

    @Autowired
    private StudyActivityRepository studyActivityRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User testUser;
    private String authToken;
    private Subject portugues;
    private Topic variacoes;

    @BeforeEach
    void setUp() {
        cleanDatabase();

        testUser = userRepository.save(new User("Aluno Rotina", "rotina@teste.com", passwordEncoder.encode("senha123")));
        authToken = jwtService.generateToken(testUser);

        portugues = subjectRepository.save(new Subject("Português", "Linguagens", "portugues", "#2563EB", "📔", testUser));
        variacoes = topicRepository.save(new Topic("Variações linguísticas", "Norma e variação", portugues));
    }

    // Os outros testes nao apagam agenda/chat; limpamos ao final para nao travar a exclusao de usuarios deles
    @AfterEach
    void tearDown() {
        cleanDatabase();
    }

    private void cleanDatabase() {
        scheduleRepository.deleteAll();
        chatMessageRepository.deleteAll();
        studyActivityRepository.deleteAll();
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
    @DisplayName("Agenda: cria ligado ao assunto (nome/cor/ícone vêm da matéria), grava a data real e edita (PUT)")
    void shouldCreateAndUpdateScheduleLinkedToTopic() throws Exception {
        String created = mockMvc.perform(post("/api/schedules")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Revisar variações\",\"topicId\":" + variacoes.getId()
                                + ",\"daysAhead\":1,\"scheduledTime\":\"14:00\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.subjectId").value(portugues.getId()))
                .andExpect(jsonPath("$.topicId").value(variacoes.getId()))
                .andExpect(jsonPath("$.subjectName").value("Português"))
                .andExpect(jsonPath("$.colorKey").value("portugues"))
                .andExpect(jsonPath("$.icon").value("📔"))
                .andExpect(jsonPath("$.daysAhead").value(1))
                .andExpect(jsonPath("$.scheduledDate").value(LocalDate.now(ZONE).plusDays(1).toString()))
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(created).get("id").asLong();

        String today = LocalDate.now(ZONE).toString();
        mockMvc.perform(put("/api/schedules/" + id)
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Revisão adiantada\",\"subjectId\":" + portugues.getId()
                                + ",\"scheduledDate\":\"" + today + "\",\"scheduledTime\":\"09:00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Revisão adiantada"))
                .andExpect(jsonPath("$.daysAhead").value(0))
                .andExpect(jsonPath("$.topicId").doesNotExist())
                .andExpect(jsonPath("$.scheduledTime").value("09:00"));
    }

    @Test
    @DisplayName("Agenda: não permite ligar a matéria de outro estudante (404)")
    void shouldRejectScheduleWithForeignSubject() throws Exception {
        User other = userRepository.save(new User("Outro", "outro@teste.com", passwordEncoder.encode("senha123")));
        Subject foreign = subjectRepository.save(new Subject("Química", "Outra pessoa", other));

        mockMvc.perform(post("/api/schedules")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Invasão\",\"subjectId\":" + foreign.getId() + "}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Sequência: começar um assunto conta o dia; dias seguidos somam e um dia sem estudo zera")
    void shouldCalculateStreak() throws Exception {
        LocalDate today = LocalDate.now(ZONE);
        studyActivityRepository.save(new StudyActivity(testUser, today.minusDays(1), ActivityType.QUIZ));
        studyActivityRepository.save(new StudyActivity(testUser, today.minusDays(2), ActivityType.CHAT));
        studyActivityRepository.save(new StudyActivity(testUser, today.minusDays(4), ActivityType.QUIZ));

        // Ainda não estudou hoje: a sequência vale até ontem (2 dias)
        mockMvc.perform(get("/api/users/me").header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.streakDays").value(2));

        mockMvc.perform(patch("/api/topics/" + variacoes.getId() + "/status")
                        .param("status", "andamento")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/dashboard").header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.streakDays").value(3))
                .andExpect(jsonPath("$.continueSubjectId").value(portugues.getId()))
                .andExpect(jsonPath("$.continueTopicId").value(variacoes.getId()));
    }

    @Test
    @DisplayName("Assunto: recusa status inválido (400)")
    void shouldRejectInvalidTopicStatus() throws Exception {
        mockMvc.perform(patch("/api/topics/" + variacoes.getId() + "/status")
                        .param("status", "feito")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Progresso: sem simulados devolve zeros e matérias sem dados; com simulado mostra a matéria e conta o dia")
    void shouldBuildProgressOverview() throws Exception {
        mockMvc.perform(get("/api/performance/overview").header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overallAccuracyRate").value(0.0))
                .andExpect(jsonPath("$.evaluationsCount").value(0))
                .andExpect(jsonPath("$.streakDays").value(0))
                .andExpect(jsonPath("$.subjects", hasSize(1)))
                .andExpect(jsonPath("$.subjects[0].hasData").value(false))
                .andExpect(jsonPath("$.bestSubject").doesNotExist());

        // Gera e responde um simulado pela API (primeira alternativa de cada questão)
        String quizJson = mockMvc.perform(post("/api/quizzes/generate")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"topicId\":" + variacoes.getId() + ",\"questionCount\":3}"))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
        JsonNode quiz = objectMapper.readTree(quizJson);
        StringBuilder answers = new StringBuilder();
        for (JsonNode q : quiz.get("questions")) {
            if (answers.length() > 0) answers.append(',');
            answers.append("{\"questionId\":").append(q.get("id").asLong())
                    .append(",\"selectedOptionId\":").append(q.get("options").get(0).get("id").asLong()).append('}');
        }
        int total = quiz.get("questions").size();

        mockMvc.perform(post("/api/quizzes/" + quiz.get("id").asLong() + "/submit")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"answers\":[" + answers + "]}"))
                .andExpect(status().isOk());

        topicRepository.save(new Topic("Figuras de linguagem", "Concluído", "concluido", portugues));

        mockMvc.perform(get("/api/performance/overview").header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evaluationsCount").value(1))
                .andExpect(jsonPath("$.topicsStudied").value(1))
                .andExpect(jsonPath("$.streakDays").value(1))
                .andExpect(jsonPath("$.subjects[0].hasData").value(true))
                .andExpect(jsonPath("$.subjects[0].questionsAnswered").value(total))
                .andExpect(jsonPath("$.subjects[0].primaryColor").value("#2563EB"))
                .andExpect(jsonPath("$.bestSubject.subjectName").value("Português"))
                .andExpect(jsonPath("$.needsReview").doesNotExist());
    }

    @Test
    @DisplayName("Chat: salva pergunta e resposta no histórico, filtra por matéria e apaga")
    void shouldSaveAndClearChatHistory() throws Exception {
        mockMvc.perform(post("/api/ai/chat")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"subjectId\":" + portugues.getId()
                                + ",\"messages\":[{\"role\":\"user\",\"content\":\"O que é variação linguística?\"}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").isNotEmpty());

        mockMvc.perform(get("/api/ai/chat/history")
                        .param("subjectId", String.valueOf(portugues.getId()))
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].role").value("user"))
                .andExpect(jsonPath("$[0].content").value("O que é variação linguística?"))
                .andExpect(jsonPath("$[1].role").value("assistant"));

        mockMvc.perform(delete("/api/ai/chat/history")
                        .param("subjectId", String.valueOf(portugues.getId()))
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/ai/chat/history").header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
