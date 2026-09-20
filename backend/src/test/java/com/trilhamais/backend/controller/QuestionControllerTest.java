package com.trilhamais.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trilhamais.backend.config.JwtService;
import com.trilhamais.backend.dto.OptionRequest;
import com.trilhamais.backend.dto.QuestionRequest;
import com.trilhamais.backend.model.Subject;
import com.trilhamais.backend.model.Topic;
import com.trilhamais.backend.model.User;
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

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class QuestionControllerTest {

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

        testUser = new User();
        testUser.setName("Pedro Alves");
        testUser.setEmail("pedro.quiz@teste.com");
        testUser.setPassword(passwordEncoder.encode("senha123"));
        testUser = userRepository.save(testUser);

        authToken = jwtService.generateToken(testUser);

        Subject subject = new Subject("Engenharia de Software", "Conceitos de arquitetura e design", testUser);
        subject = subjectRepository.save(subject);

        testTopic = new Topic("Padrões de Projeto", "GoF, SOLID e Clean Architecture", subject);
        testTopic = topicRepository.save(testTopic);
    }

    @Test
    @DisplayName("Deve cadastrar uma questão com 4 alternativas com sucesso (201)")
    void shouldCreateQuestionSuccessfully() throws Exception {
        QuestionRequest request = new QuestionRequest(
                "O que prega o princípio da responsabilidade única (SRP)?",
                "O SRP define que uma classe deve ter um e apenas um motivo para mudar.",
                List.of(
                        new OptionRequest("Uma classe deve ter um único motivo para mudar.", true),
                        new OptionRequest("Uma classe deve conter todas as regras de negócio do sistema.", false),
                        new OptionRequest("Uma interface deve implementar todos os métodos possíveis.", false),
                        new OptionRequest("Classes filhas não podem herdar de classes abstratas.", false)
                )
        );

        mockMvc.perform(post("/api/topics/" + testTopic.getId() + "/questions")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.text").value("O que prega o princípio da responsabilidade única (SRP)?"))
                .andExpect(jsonPath("$.explanation").value("O SRP define que uma classe deve ter um e apenas um motivo para mudar."))
                .andExpect(jsonPath("$.topicId").value(testTopic.getId()))
                .andExpect(jsonPath("$.options", hasSize(4)))
                .andExpect(jsonPath("$.options[0].isCorrect").value(true));
    }

    @Test
    @DisplayName("Deve rejeitar criação de questão sem nenhuma alternativa correta (400)")
    void shouldRejectQuestionWithoutCorrectOption() throws Exception {
        QuestionRequest request = new QuestionRequest(
                "Qual a cor do céu?",
                "Explicação",
                List.of(
                        new OptionRequest("Verde", false),
                        new OptionRequest("Amarelo", false)
                )
        );

        mockMvc.perform(post("/api/topics/" + testTopic.getId() + "/questions")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("exatamente uma alternativa correta")));
    }

    @Test
    @DisplayName("Deve rejeitar criação de questão com mais de uma alternativa correta (400)")
    void shouldRejectQuestionWithMultipleCorrectOptions() throws Exception {
        QuestionRequest request = new QuestionRequest(
                "Selecione uma verdade:",
                "Explicação",
                List.of(
                        new OptionRequest("2 + 2 = 4", true),
                        new OptionRequest("3 + 3 = 6", true)
                )
        );

        mockMvc.perform(post("/api/topics/" + testTopic.getId() + "/questions")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("exatamente uma alternativa correta")));
    }

    @Test
    @DisplayName("Deve rejeitar criação de questão com menos de 2 alternativas (400)")
    void shouldRejectQuestionWithLessThanTwoOptions() throws Exception {
        QuestionRequest request = new QuestionRequest(
                "Questão incompleta",
                "Explicação",
                List.of(
                        new OptionRequest("Única opção", true)
                )
        );

        mockMvc.perform(post("/api/topics/" + testTopic.getId() + "/questions")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar cadastrar questão em tópico inexistente")
    void shouldReturn404WhenTopicNotFound() throws Exception {
        QuestionRequest request = new QuestionRequest(
                "Enunciado qualquer",
                "Explicação",
                List.of(
                        new OptionRequest("Opção A", true),
                        new OptionRequest("Opção B", false)
                )
        );

        mockMvc.perform(post("/api/topics/99999/questions")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve listar questões cadastradas do tópico (200)")
    void shouldListQuestionsByTopic() throws Exception {
        QuestionRequest request = new QuestionRequest(
                "O que é Inversão de Controle (IoC)?",
                "Padrão que delega o controle do fluxo ao container.",
                List.of(
                        new OptionRequest("Delegação do controle de instanciação para um framework.", true),
                        new OptionRequest("Uso obrigatório de ponteiros em C.", false)
                )
        );

        mockMvc.perform(post("/api/topics/" + testTopic.getId() + "/questions")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/topics/" + testTopic.getId() + "/questions")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].text").value("O que é Inversão de Controle (IoC)?"));
    }

    @Test
    @DisplayName("Deve obter detalhes da questão por ID (200)")
    void shouldGetQuestionById() throws Exception {
        QuestionRequest request = new QuestionRequest(
                "O que significa ACID?",
                "Atomicidade, Consistência, Isolamento e Durabilidade.",
                List.of(
                        new OptionRequest("Propriedades transacionais de bancos de dados.", true),
                        new OptionRequest("Um tipo de compressão de arquivos.", false)
                )
        );

        String responseStr = mockMvc.perform(post("/api/topics/" + testTopic.getId() + "/questions")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long questionId = objectMapper.readTree(responseStr).get("id").asLong();

        mockMvc.perform(get("/api/questions/" + questionId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(questionId))
                .andExpect(jsonPath("$.text").value("O que significa ACID?"));
    }

    @Test
    @DisplayName("Deve deletar questão com sucesso (204)")
    void shouldDeleteQuestion() throws Exception {
        QuestionRequest request = new QuestionRequest(
                "Questão descartável",
                "Será apagada",
                List.of(
                        new OptionRequest("A", true),
                        new OptionRequest("B", false)
                )
        );

        String responseStr = mockMvc.perform(post("/api/topics/" + testTopic.getId() + "/questions")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long questionId = objectMapper.readTree(responseStr).get("id").asLong();

        mockMvc.perform(delete("/api/questions/" + questionId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/questions/" + questionId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Isolamento Multitenancy: Usuário B não pode acessar questões criadas pelo Usuário A")
    void shouldIsolateQuestionsBetweenUsers() throws Exception {
        QuestionRequest request = new QuestionRequest(
                "Questão exclusiva do Usuário A",
                "Privada",
                List.of(
                        new OptionRequest("Opção 1", true),
                        new OptionRequest("Opção 2", false)
                )
        );

        String responseStr = mockMvc.perform(post("/api/topics/" + testTopic.getId() + "/questions")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long questionId = objectMapper.readTree(responseStr).get("id").asLong();

        // Cria Usuario B
        User userB = new User();
        userB.setName("Outro Aluno");
        userB.setEmail("aluno.b@teste.com");
        userB.setPassword(passwordEncoder.encode("senha123"));
        userB = userRepository.save(userB);
        String tokenB = jwtService.generateToken(userB);

        mockMvc.perform(get("/api/questions/" + questionId)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());
    }
}
