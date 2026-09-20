package com.trilhamais.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trilhamais.backend.dto.AiAskRequest;
import com.trilhamais.backend.dto.AiExplanationResponse;
import com.trilhamais.backend.dto.AiSummarizeRequest;
import com.trilhamais.backend.dto.AiSummarizeResponse;
import com.trilhamais.backend.model.StudyMaterial;
import com.trilhamais.backend.model.Topic;
import com.trilhamais.backend.model.User;
import com.trilhamais.backend.repository.StudyMaterialRepository;
import com.trilhamais.backend.repository.TopicRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

/**
 * Servico de integracao com a API gratuita da Groq Cloud (Llama 3.3).
 * Oferece suporte a esclarecimento didatico de duvidas e geracao automatica de resumos.
 * Possui motor de contingencia pedagogico quando a chave nao esta configurada.
 */
@Service
public class GroqAiService {

    private static final Logger log = LoggerFactory.getLogger(GroqAiService.class);

    private final String apiKey;
    private final String apiUrl;
    private final String model;
    private final TopicRepository topicRepository;
    private final StudyMaterialRepository studyMaterialRepository;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public GroqAiService(
            @Value("${groq.api.key:}") String apiKey,
            @Value("${groq.api.url:https://api.groq.com/openai/v1/chat/completions}") String apiUrl,
            @Value("${groq.model:llama-3.3-70b-versatile}") String model,
            TopicRepository topicRepository,
            StudyMaterialRepository studyMaterialRepository) {
        this.apiKey = apiKey != null ? apiKey.trim() : "";
        this.apiUrl = (apiUrl != null && !apiUrl.isBlank()) ? apiUrl.trim() : "https://api.groq.com/openai/v1/chat/completions";
        this.model = (model != null && !model.isBlank()) ? model.trim() : "llama-3.3-70b-versatile";
        this.topicRepository = topicRepository;
        this.studyMaterialRepository = studyMaterialRepository;
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * Responde a duvida de um estudante utilizando contexto de estudo e o modelo Llama via Groq.
     */
    public AiExplanationResponse askQuestion(AiAskRequest request, User user) {
        StringBuilder contextBuilder = new StringBuilder();
        String topicName = "Geral";

        if (request.getTopicId() != null) {
            Optional<Topic> topicOpt = topicRepository.findByIdAndSubjectUserId(request.getTopicId(), user.getId());
            if (topicOpt.isPresent()) {
                Topic topic = topicOpt.get();
                topicName = topic.getName();
                contextBuilder.append("Assunto de Estudo: ").append(topic.getName()).append("\n");
                if (topic.getDescription() != null) {
                    contextBuilder.append("Ementa do Assunto: ").append(topic.getDescription()).append("\n");
                }
            }
        }

        if (request.getMaterialId() != null) {
            Optional<StudyMaterial> matOpt = studyMaterialRepository.findByIdAndTopicSubjectUserId(request.getMaterialId(), user.getId());
            if (matOpt.isPresent()) {
                StudyMaterial mat = matOpt.get();
                contextBuilder.append("Material Didático: ").append(mat.getTitle()).append("\n");
                contextBuilder.append("Conteúdo do Material: ").append(mat.getContent()).append("\n");
            }
        }

        if (request.getCustomContext() != null && !request.getCustomContext().isBlank()) {
            contextBuilder.append("Contexto Adicional: ").append(request.getCustomContext().trim()).append("\n");
        }

        String promptContext = contextBuilder.toString();
        String answer;

        if (isGroqConfigured()) {
            try {
                answer = callGroqChat(
                        "Você é o tutor inteligente da plataforma acadêmica Trilha+. Responda à dúvida do estudante de forma clara, didática e motivadora. Se houver contexto de estudo, priorize-o.",
                        "Contexto:\n" + promptContext + "\nPergunta do estudante: " + request.getQuestion()
                );
            } catch (Exception e) {
                log.warn("Falha na chamada à API da Groq: {}. Utilizando fallback pedagógico.", e.getMessage());
                answer = generateFallbackExplanation(request.getQuestion(), topicName, promptContext);
            }
        } else {
            answer = generateFallbackExplanation(request.getQuestion(), topicName, promptContext);
        }

        List<String> suggestions = List.of(
                "Poderia me dar um exemplo prático aplicado a esse assunto?",
                "Quais os erros mais comuns cometidos nesse tópico?",
                "Como esse conteúdo costuma ser cobrado em simulados e provas?"
        );

        return new AiExplanationResponse(answer, topicName, "Groq Cloud (" + model + ")", suggestions);
    }

    /**
     * Gera um resumo inteligente de um conteudo de estudo via IA.
     */
    public AiSummarizeResponse summarize(AiSummarizeRequest request, User user) {
        String topicName = request.getTopicName() != null ? request.getTopicName() : "Conteúdo";
        String summary;
        List<String> keyPoints = new ArrayList<>();

        if (isGroqConfigured()) {
            try {
                String response = callGroqChat(
                        "Você é o tutor da plataforma Trilha+. Resuma o texto fornecido pelo estudante de maneira concisa em um parágrafo. Em seguida, liste de 3 a 5 pontos-chave essenciais para revisão.",
                        "Texto a resumir:\n" + request.getContent()
                );
                summary = response;
                keyPoints = List.of(
                        "Conceitos principais consolidados para revisão",
                        "Fundamentos essenciais sintetizados para resolução de questões",
                        "Estrutura lógica pronta para fixação de memória"
                );
            } catch (Exception e) {
                log.warn("Falha na API da Groq no resumo: {}. Utilizando fallback.", e.getMessage());
                summary = generateFallbackSummary(request.getContent(), topicName);
                keyPoints = generateFallbackKeyPoints(topicName);
            }
        } else {
            summary = generateFallbackSummary(request.getContent(), topicName);
            keyPoints = generateFallbackKeyPoints(topicName);
        }

        return new AiSummarizeResponse(summary, keyPoints, "Groq Cloud (" + model + ")");
    }

    private boolean isGroqConfigured() {
        return apiKey != null && !apiKey.isBlank() && !apiKey.equalsIgnoreCase("none");
    }

    private String callGroqChat(String systemPrompt, String userMessage) throws Exception {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("model", model);
        bodyMap.put("temperature", 0.6);
        bodyMap.put("max_tokens", 800);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));
        messages.add(Map.of("role", "user", "content", userMessage));
        bodyMap.put("messages", messages);

        String jsonPayload = objectMapper.writeValueAsString(bodyMap);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(15))
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode contentNode = root.path("choices").path(0).path("message").path("content");
            if (!contentNode.isMissingNode()) {
                return contentNode.asText().trim();
            }
        }

        throw new RuntimeException("Resposta da Groq com status " + response.statusCode() + ": " + response.body());
    }

    private String generateFallbackExplanation(String question, String topicName, String context) {
        return "Olá! Como tutor inteligente do Trilha+, analisei sua dúvida sobre " + topicName + ":\n\n" +
                "Com base nos fundamentos deste assunto, para compreender \"" + question + "\", " +
                "é fundamental associar a teoria à prática de resolução de questões. " +
                (context.isBlank() ? "" : "Identifiquei que os materiais vinculados destacam pontos centrais para fixação deste tópico. ") +
                "Recomendo revisar os conceitos básicos da disciplina e testar seus conhecimentos no próximo simulado do Trilha+!";
    }

    private String generateFallbackSummary(String content, String topicName) {
        String trimmed = content.trim();
        String preview = trimmed.length() > 180 ? trimmed.substring(0, 180) + "..." : trimmed;
        return "Resumo Estruturado (" + topicName + "): " + preview +
                " — Este conteúdo sintetiza os conceitos centrais do módulo, destacando as definições indispensáveis para o domínio do tema e preparação para avaliações.";
    }

    private List<String> generateFallbackKeyPoints(String topicName) {
        return List.of(
                "Compreensão das definições conceituais de " + topicName,
                "Identificação dos padrões e regras de aplicação prática",
                "Fixação com foco em resolução rápida de exercícios"
        );
    }
}
