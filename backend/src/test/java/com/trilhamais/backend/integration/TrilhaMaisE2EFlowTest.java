package com.trilhamais.backend.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trilhamais.backend.dto.*;
import com.trilhamais.backend.model.MaterialType;
import com.trilhamais.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Teste de Integracao de Ponta a Ponta (E2E) simulando a jornada real completa de um estudante
 * utilizando a aplicacao Trilha+ (mobile React Native conectando as APIs Spring Boot):
 *
 * 1. Cadastro de Estudante
 * 2. Login com geracao de Token JWT
 * 3. Consulta de Perfil Protegido
 * 4. Criacao de Materia
 * 5. Criacao de Assunto (Topico)
 * 6. Cadastro de Material de Estudo
 * 7. Cadastro de Banco de Questoes (10 questoes)
 * 8. Consulta ao Tutor de IA (Groq Cloud)
 * 9. Resumo de Material com IA
 * 10. Geracao de Simulado Avaliativo (com sigilo anti-fraude)
 * 11. Submissao de Respostas com Correcao Automatica e Nota
 * 12. Painel de Desempenho e Metricas Globais
 * 13. Diagnostico de Topicos Fracos e Recomendacoes Pedagogicas
 * 14. Acesso publico a documentacao Swagger / OpenAPI 3.0
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TrilhaMaisE2EFlowTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionOptionRepository questionOptionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private StudyMaterialRepository studyMaterialRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

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
    @DisplayName("Jornada E2E completa do estudante: Cadastro -> Login -> Estudos -> IA -> Quiz -> Desempenho")
    void testCompleteStudentJourneyE2E() throws Exception {
        // =========================================================================
        // PASSO 1: Cadastro do Estudante
        // =========================================================================
        RegisterRequest registerReq = new RegisterRequest(
                "Aluno E2E",
                "aluno.e2e@uninassau.edu.br",
                "senhaForte123"
        );

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.email", is("aluno.e2e@uninassau.edu.br")))
                .andExpect(jsonPath("$.name", is("Aluno E2E")));

        // =========================================================================
        // PASSO 2: Login com Autenticação e Obtenção do Token JWT
        // =========================================================================
        LoginRequest loginReq = new LoginRequest("aluno.e2e@uninassau.edu.br", "senhaForte123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType", is("Bearer")))
                .andReturn();

        JsonNode loginJson = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String token = loginJson.get("accessToken").asText();
        assertNotNull(token);

        String authHeader = "Bearer " + token;

        // =========================================================================
        // PASSO 3: Consulta de Perfil Protegido do Usuário Logado
        // =========================================================================
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("aluno.e2e@uninassau.edu.br")))
                .andExpect(jsonPath("$.name", is("Aluno E2E")));

        // =========================================================================
        // PASSO 4: Cadastro de Matéria Acadêmica
        // =========================================================================
        SubjectRequest subjectReq = new SubjectRequest("Estrutura de Dados", "Disciplina prática de algoritmos e árvores");

        MvcResult subjectResult = mockMvc.perform(post("/api/subjects")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subjectReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Estrutura de Dados")))
                .andReturn();

        Long subjectId = objectMapper.readTree(subjectResult.getResponse().getContentAsString()).get("id").asLong();

        // =========================================================================
        // PASSO 5: Cadastro de Assunto (Tópico)
        // =========================================================================
        TopicRequest topicReq = new TopicRequest("Árvores Binárias de Busca", "Propriedades e algoritmos de balanceamento AVL");

        MvcResult topicResult = mockMvc.perform(post("/api/subjects/" + subjectId + "/topics")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(topicReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Árvores Binárias de Busca")))
                .andReturn();

        Long topicId = objectMapper.readTree(topicResult.getResponse().getContentAsString()).get("id").asLong();

        // =========================================================================
        // PASSO 6: Cadastro de Material Didático
        // =========================================================================
        StudyMaterialRequest materialReq = new StudyMaterialRequest(
                "Apostila de Árvores AVL",
                "Uma árvore AVL é uma árvore binária de busca auto-balanceada onde as alturas diferem no máximo em 1.",
                MaterialType.NOTES
        );

        MvcResult materialResult = mockMvc.perform(post("/api/topics/" + topicId + "/materials")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(materialReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Apostila de Árvores AVL")))
                .andReturn();

        Long materialId = objectMapper.readTree(materialResult.getResponse().getContentAsString()).get("id").asLong();

        // =========================================================================
        // PASSO 7: Cadastro de 10 Questões com Alternativas para o Tópico
        // =========================================================================
        for (int i = 1; i <= 10; i++) {
            List<OptionRequest> options = List.of(
                    new OptionRequest("Alternativa Correta da Questão " + i, true),
                    new OptionRequest("Alternativa Incorreta A", false),
                    new OptionRequest("Alternativa Incorreta B", false),
                    new OptionRequest("Alternativa Incorreta C", false)
            );
            QuestionRequest questionReq = new QuestionRequest(
                    "Enunciado da questão número " + i + " sobre Árvores Binárias?",
                    "Explicação didática detalhada para a questão " + i,
                    options
            );

            mockMvc.perform(post("/api/topics/" + topicId + "/questions")
                            .header("Authorization", authHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(questionReq)))
                    .andExpect(status().isCreated());
        }

        // =========================================================================
        // PASSO 8: Consulta de Dúvida com a IA (Groq Cloud)
        // =========================================================================
        AiAskRequest aiReq = new AiAskRequest("Como funciona a rotação à esquerda na árvore AVL?", topicId, null, null);

        mockMvc.perform(post("/api/ai/ask")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aiReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").isNotEmpty())
                .andExpect(jsonPath("$.modelProvider").isNotEmpty());

        // =========================================================================
        // PASSO 9: Resumo de Material com a IA
        // =========================================================================
        AiSummarizeRequest sumReq = new AiSummarizeRequest(
                "Uma árvore AVL é uma árvore binária de busca auto-balanceada onde as alturas diferem no máximo em 1.",
                "Árvores Binárias de Busca"
        );

        mockMvc.perform(post("/api/ai/summarize")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sumReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary").isNotEmpty());

        // =========================================================================
        // PASSO 10: Geração de Simulado Avaliativo (10 questões)
        // =========================================================================
        QuizGenerateRequest quizGenReq = new QuizGenerateRequest(topicId, null, 10, "Simulado Oficial E2E");

        MvcResult quizGenResult = mockMvc.perform(post("/api/quizzes/generate")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizGenReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalQuestions", is(10)))
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")))
                .andExpect(jsonPath("$.questions", hasSize(10)))
                // Garantia anti-fraude: gabarito e explicacao nao podem vazar durante a prova!
                .andExpect(jsonPath("$.questions[0].explanation").doesNotExist())
                .andExpect(jsonPath("$.questions[0].options[0].isCorrect").doesNotExist())
                .andReturn();

        JsonNode quizJson = objectMapper.readTree(quizGenResult.getResponse().getContentAsString());
        Long quizId = quizJson.get("id").asLong();

        // =========================================================================
        // PASSO 11: Submissão de Respostas (8 acertos e 2 erros planejados)
        // =========================================================================
        List<AnswerItemRequest> submissions = new ArrayList<>();
        JsonNode questionsArray = quizJson.get("questions");

        for (int i = 0; i < questionsArray.size(); i++) {
            JsonNode qNode = questionsArray.get(i);
            Long qId = qNode.get("id").asLong();
            JsonNode opts = qNode.get("options");

            // Nas primeiras 8 questoes, seleciona a primeira opcao (que e a correta)
            // Nas ultimas 2 questoes, seleciona a segunda opcao (incorreta)
            Long chosenOptionId;
            if (i < 8) {
                chosenOptionId = opts.get(0).get("id").asLong();
            } else {
                chosenOptionId = opts.get(1).get("id").asLong();
            }
            submissions.add(new AnswerItemRequest(qId, chosenOptionId));
        }

        QuizSubmitRequest submitReq = new QuizSubmitRequest(submissions);

        mockMvc.perform(post("/api/quizzes/" + quizId + "/submit")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("COMPLETED")))
                .andExpect(jsonPath("$.totalQuestions", is(10)))
                .andExpect(jsonPath("$.correctAnswers", is(8)))
                .andExpect(jsonPath("$.score", is(8.0)))
                .andExpect(jsonPath("$.percentage", is(80.0)))
                .andExpect(jsonPath("$.results", hasSize(10)));

        // =========================================================================
        // PASSO 12: Painel Geral de Desempenho (/api/performance/summary)
        // =========================================================================
        mockMvc.perform(get("/api/performance/summary")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalQuizzesCompleted", is(1)))
                .andExpect(jsonPath("$.totalQuestionsAnswered", is(10)))
                .andExpect(jsonPath("$.totalCorrectAnswers", is(8)))
                .andExpect(jsonPath("$.averageScore", is(8.0)))
                .andExpect(jsonPath("$.overallAccuracyRate", is(80.0)))
                .andExpect(jsonPath("$.recentScores", hasSize(1)));

        // =========================================================================
        // PASSO 13: Diagnóstico por Assunto e Recomendações Inteligentes
        // =========================================================================
        mockMvc.perform(get("/api/performance/topics")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].topicName", is("Árvores Binárias de Busca")))
                .andExpect(jsonPath("$[0].accuracyRate", is(80.0)))
                .andExpect(jsonPath("$[0].masteryLevel", is("MASTERED")));

        mockMvc.perform(get("/api/performance/recommendations")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aiStudyPlan").isNotEmpty())
                .andExpect(jsonPath("$.aiProvider").isNotEmpty());
    }

    @Test
    @DisplayName("Documentação OpenAPI 3.0 / Swagger UI acessível publicamente sem token")
    void testOpenApiAndSwaggerEndpointsAccessibleWithoutToken() throws Exception {
        // Documentação JSON bruta OpenAPI v3
        MvcResult apiDocsResult = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode docsJson = objectMapper.readTree(apiDocsResult.getResponse().getContentAsString());
        assertNotNull(docsJson.get("openapi"));
        assertEquals("Trilha+ API — Documentação Oficial", docsJson.get("info").get("title").asText());

        // Interface interativa do Swagger UI
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }
}
