package com.trilhamais.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trilhamais.backend.config.JwtService;
import com.trilhamais.backend.model.*;
import com.trilhamais.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PerformanceControllerTest {

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

    private User testUser;
    private String authToken;
    private Subject testSubject;
    private Topic topicCritical;
    private Topic topicMastered;

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
        testUser.setEmail("pedro.desempenho@teste.com");
        testUser.setPassword(passwordEncoder.encode("senha123"));
        testUser = userRepository.save(testUser);

        authToken = jwtService.generateToken(testUser);

        testSubject = new Subject("Engenharia de Software", "Conceitos e Práticas", testUser);
        testSubject = subjectRepository.save(testSubject);

        topicCritical = new Topic("Refatoração", "Code smells e boas práticas", testSubject);
        topicCritical = topicRepository.save(topicCritical);

        topicMastered = new Topic("Controle de Versão", "Git e GitHub", testSubject);
        topicMastered = topicRepository.save(topicMastered);
    }

    @Test
    @DisplayName("Deve retornar resumo zerado e seguro quando o estudante ainda não concluiu simulados (200)")
    void shouldReturnDefaultSummaryWhenNoQuizzesCompleted() throws Exception {
        mockMvc.perform(get("/api/performance/summary")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalQuizzesCompleted").value(0))
                .andExpect(jsonPath("$.totalQuestionsAnswered").value(0))
                .andExpect(jsonPath("$.totalCorrectAnswers").value(0))
                .andExpect(jsonPath("$.averageScore").value(0.0))
                .andExpect(jsonPath("$.overallAccuracyRate").value(0.0))
                .andExpect(jsonPath("$.recentScores", hasSize(0)));
    }

    @Test
    @DisplayName("Deve calcular resumo geral com média e aproveitamento precisos a partir de simulados finalizados")
    void shouldCalculateAccurateSummaryWithCompletedQuizzes() throws Exception {
        // Simulado 1: Nota 10.0 (2 de 2 acertos)
        Quiz q1 = new Quiz("Simulado 1", testUser, topicMastered, testSubject, 2);
        q1.setCorrectAnswers(2);
        q1.setScore(10.0);
        q1.setStatus(QuizStatus.COMPLETED);
        q1.setCompletedAt(LocalDateTime.now().minusHours(2));
        quizRepository.save(q1);

        // Simulado 2: Nota 6.0 (3 de 5 acertos)
        Quiz q2 = new Quiz("Simulado 2", testUser, topicCritical, testSubject, 5);
        q2.setCorrectAnswers(3);
        q2.setScore(6.0);
        q2.setStatus(QuizStatus.COMPLETED);
        q2.setCompletedAt(LocalDateTime.now().minusHours(1));
        quizRepository.save(q2);

        // Simulado 3 em andamento (IN_PROGRESS) - NÃO deve entrar no cálculo
        Quiz q3 = new Quiz("Simulado Em Andamento", testUser, topicCritical, testSubject, 10);
        q3.setStatus(QuizStatus.IN_PROGRESS);
        quizRepository.save(q3);

        mockMvc.perform(get("/api/performance/summary")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalQuizzesCompleted").value(2))
                .andExpect(jsonPath("$.totalQuestionsAnswered").value(7))
                .andExpect(jsonPath("$.totalCorrectAnswers").value(5))
                .andExpect(jsonPath("$.averageScore").value(8.0))
                .andExpect(jsonPath("$.overallAccuracyRate").value(71.4))
                .andExpect(jsonPath("$.recentScores", hasSize(2)))
                .andExpect(jsonPath("$.recentScores[0]").value(6.0))
                .andExpect(jsonPath("$.recentScores[1]").value(10.0));
    }

    @Test
    @DisplayName("Deve diagnosticar pontos de dificuldade por tópico ordenando o mais crítico primeiro")
    void shouldIdentifyCriticalAndMasteredTopicsAccurately() throws Exception {
        // Questão do Tópico Crítico (Errou todas -> 0% de acerto)
        Question questionCrit = new Question("O que é Long Method?", "Método com linhas excessivas", topicCritical);
        QuestionOption optCritCorrect = new QuestionOption("Método com excesso de linhas de código", true);
        QuestionOption optCritWrong = new QuestionOption("Método com nome longo", false);
        questionCrit.addOption(optCritCorrect);
        questionCrit.addOption(optCritWrong);
        questionCrit = questionRepository.save(questionCrit);

        // Questão do Tópico Dominado (Acertou todas -> 100% de acerto)
        Question questionMast = new Question("O que é git pull?", "Baixa e mescla alterações", topicMastered);
        QuestionOption optMastCorrect = new QuestionOption("Atualiza a branch local com a remota", true);
        QuestionOption optMastWrong = new QuestionOption("Apaga o repositório", false);
        questionMast.addOption(optMastCorrect);
        questionMast.addOption(optMastWrong);
        questionMast = questionRepository.save(questionMast);

        Quiz quiz = new Quiz("Simulado de Diagnóstico", testUser, null, testSubject, 2);
        quiz.setCorrectAnswers(1);
        quiz.setScore(5.0);
        quiz.setStatus(QuizStatus.COMPLETED);
        quiz.setCompletedAt(LocalDateTime.now());
        quiz = quizRepository.save(quiz);

        // Registra respostas do aluno
        QuizAnswer a1 = new QuizAnswer(quiz, questionCrit, optCritWrong, false);
        QuizAnswer a2 = new QuizAnswer(quiz, questionMast, optMastCorrect, true);
        quizAnswerRepository.save(a1);
        quizAnswerRepository.save(a2);

        mockMvc.perform(get("/api/performance/topics")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                // O primeiro da lista deve ser o tópico mais crítico
                .andExpect(jsonPath("$[0].topicId").value(topicCritical.getId()))
                .andExpect(jsonPath("$[0].topicName").value("Refatoração"))
                .andExpect(jsonPath("$[0].accuracyRate").value(0.0))
                .andExpect(jsonPath("$[0].masteryLevel").value("CRITICAL"))
                // O segundo da lista deve ser o tópico dominado
                .andExpect(jsonPath("$[1].topicId").value(topicMastered.getId()))
                .andExpect(jsonPath("$[1].topicName").value("Controle de Versão"))
                .andExpect(jsonPath("$[1].accuracyRate").value(100.0))
                .andExpect(jsonPath("$[1].masteryLevel").value("MASTERED"));
    }

    @Test
    @DisplayName("Deve gerar recomendações de estudo com materiais e plano da IA Groq para os pontos críticos")
    void shouldProvideRecommendationsWithMaterialsAndAiStudyPlan() throws Exception {
        // Cadastra um material no tópico crítico
        StudyMaterial material = new StudyMaterial("Guia de Refatoração Clean Code",
                "Passo a passo para eliminar code smells e métodos longos.",
                MaterialType.ARTICLE, topicCritical);
        studyMaterialRepository.save(material);

        Question questionCrit = new Question("O que é Feature Envy?", "Quando um método acessa mais dados de outra classe", topicCritical);
        QuestionOption optWrong = new QuestionOption("Inveja de novos recursos", false);
        questionCrit.addOption(new QuestionOption("Acesso excessivo aos dados de outro objeto", true));
        questionCrit.addOption(optWrong);
        questionCrit = questionRepository.save(questionCrit);

        Quiz quiz = new Quiz("Simulado Avaliativo", testUser, topicCritical, testSubject, 1);
        quiz.setCorrectAnswers(0);
        quiz.setScore(0.0);
        quiz.setStatus(QuizStatus.COMPLETED);
        quiz.setCompletedAt(LocalDateTime.now());
        quiz = quizRepository.save(quiz);

        QuizAnswer answer = new QuizAnswer(quiz, questionCrit, optWrong, false);
        quizAnswerRepository.save(answer);

        mockMvc.perform(get("/api/performance/recommendations")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.criticalTopics", hasSize(1)))
                .andExpect(jsonPath("$.criticalTopics[0].topicName").value("Refatoração"))
                .andExpect(jsonPath("$.recommendedMaterials", hasSize(1)))
                .andExpect(jsonPath("$.recommendedMaterials[0].title").value("Guia de Refatoração Clean Code"))
                .andExpect(jsonPath("$.aiStudyPlan").isNotEmpty())
                .andExpect(jsonPath("$.aiProvider").isNotEmpty());
    }

    @Test
    @DisplayName("Isolamento Multitenancy: Estudante B não visualiza métricas de desempenho do Estudante A")
    void shouldIsolatePerformanceDataBetweenUsers() throws Exception {
        // Cria simulado concluído para o Estudante A
        Quiz quizA = new Quiz("Simulado Aluno A", testUser, topicMastered, testSubject, 10);
        quizA.setCorrectAnswers(10);
        quizA.setScore(10.0);
        quizA.setStatus(QuizStatus.COMPLETED);
        quizA.setCompletedAt(LocalDateTime.now());
        quizRepository.save(quizA);

        // Cria Estudante B
        User userB = new User();
        userB.setName("Estudante Novo");
        userB.setEmail("aluno.b.desempenho@teste.com");
        userB.setPassword(passwordEncoder.encode("senha123"));
        userB = userRepository.save(userB);
        String tokenB = jwtService.generateToken(userB);

        // Estudante B deve ter resumo zerado
        mockMvc.perform(get("/api/performance/summary")
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalQuizzesCompleted").value(0))
                .andExpect(jsonPath("$.averageScore").value(0.0));
    }

    @Test
    @DisplayName("Deve bloquear requisições sem token de autenticação JWT (401)")
    void shouldBlockUnauthenticatedRequests() throws Exception {
        mockMvc.perform(get("/api/performance/summary"))
                .andExpect(status().isUnauthorized());
    }
}
