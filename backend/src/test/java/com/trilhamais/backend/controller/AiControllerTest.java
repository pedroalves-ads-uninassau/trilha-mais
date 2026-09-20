package com.trilhamais.backend.controller;

import com.trilhamais.backend.config.JwtService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AiControllerTest {

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

        Subject subject = subjectRepository.save(new Subject("Redes de Computadores", "Protocolos", testUser));
        testTopic = topicRepository.save(new Topic("Modelo OSI e TCP/IP", "Camadas de rede", subject));
    }

    @Test
    @DisplayName("Teste 1: Envio de duvida para IA deve retornar 200 OK e explicacao estruturada")
    void shouldAskAiQuestionSuccessfully() throws Exception {
        String json = """
            {
              "question": "Qual a diferenca basica entre TCP e UDP?"
            }
            """;

        mockMvc.perform(post("/api/ai/ask")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer", notNullValue()))
                .andExpect(jsonPath("$.modelProvider", containsString("Groq")))
                .andExpect(jsonPath("$.suggestedQuestions", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("Teste 2: Envio de duvida com topico de contexto deve vincular o nome do assunto")
    void shouldAskAiQuestionWithTopicContext() throws Exception {
        String json = """
            {
              "topicId": %d,
              "question": "Como funciona o handshake de tres vias?"
            }
            """.formatted(testTopic.getId());

        mockMvc.perform(post("/api/ai/ask")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topicName", is("Modelo OSI e TCP/IP")))
                .andExpect(jsonPath("$.answer", notNullValue()));
    }

    @Test
    @DisplayName("Teste 3: Pergunta vazia deve retornar 400 Bad Request")
    void shouldReturnBadRequestWhenQuestionBlank() throws Exception {
        String json = """
            {
              "question": ""
            }
            """;

        mockMvc.perform(post("/api/ai/ask")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.question", notNullValue()));
    }

    @Test
    @DisplayName("Teste 4: Solicitacao de resumo deve retornar 200 OK com sintese e pontos-chave")
    void shouldSummarizeContentSuccessfully() throws Exception {
        String json = """
            {
              "content": "O protocolo HTTP e sem estado (stateless), operando na camada de aplicacao. O HTTPS adiciona a camada de seguranca TLS/SSL.",
              "topicName": "Protocolos Web"
            }
            """;

        mockMvc.perform(post("/api/ai/summarize")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary", notNullValue()))
                .andExpect(jsonPath("$.keyTakeaways", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.modelProvider", containsString("Groq")));
    }

    @Test
    @DisplayName("Teste 5: Resumo com conteudo em branco deve retornar 400 Bad Request")
    void shouldReturnBadRequestWhenSummarizeContentBlank() throws Exception {
        String json = """
            {
              "content": ""
            }
            """;

        mockMvc.perform(post("/api/ai/summarize")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.content", notNullValue()));
    }

    @Test
    @DisplayName("Teste 6: Acesso a IA sem token Bearer deve retornar 401 Unauthorized")
    void shouldReturnUnauthorizedWithoutToken() throws Exception {
        String json = """
            {
              "question": "Pergunta sem auth"
            }
            """;

        mockMvc.perform(post("/api/ai/ask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }
}
