package com.trilhamais.backend.service;

import com.trilhamais.backend.dto.PerformanceRecommendationResponse;
import com.trilhamais.backend.dto.PerformanceSummaryResponse;
import com.trilhamais.backend.dto.RecommendedMaterialDto;
import com.trilhamais.backend.dto.TopicPerformanceResponse;
import com.trilhamais.backend.model.*;
import com.trilhamais.backend.repository.QuizAnswerRepository;
import com.trilhamais.backend.repository.QuizRepository;
import com.trilhamais.backend.repository.StudyMaterialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PerformanceService {

    private final QuizRepository quizRepository;
    private final QuizAnswerRepository quizAnswerRepository;
    private final StudyMaterialRepository studyMaterialRepository;
    private final GroqAiService groqAiService;

    public PerformanceService(QuizRepository quizRepository,
                              QuizAnswerRepository quizAnswerRepository,
                              StudyMaterialRepository studyMaterialRepository,
                              GroqAiService groqAiService) {
        this.quizRepository = quizRepository;
        this.quizAnswerRepository = quizAnswerRepository;
        this.studyMaterialRepository = studyMaterialRepository;
        this.groqAiService = groqAiService;
    }

    @Transactional(readOnly = true)
    public PerformanceSummaryResponse getPerformanceSummary(User user) {
        List<Quiz> completedQuizzes = quizRepository.findByUserIdAndStatusOrderByCompletedAtDesc(user.getId(), QuizStatus.COMPLETED);

        if (completedQuizzes.isEmpty()) {
            return new PerformanceSummaryResponse(0, 0, 0, 0.0, 0.0, List.of());
        }

        int totalQuizzes = completedQuizzes.size();
        int totalQuestions = completedQuizzes.stream().mapToInt(Quiz::getTotalQuestions).sum();
        int totalCorrect = completedQuizzes.stream().mapToInt(q -> q.getCorrectAnswers() != null ? q.getCorrectAnswers() : 0).sum();

        double avgScore = completedQuizzes.stream()
                .mapToDouble(q -> q.getScore() != null ? q.getScore() : 0.0)
                .average()
                .orElse(0.0);
        double roundedAvgScore = Math.round(avgScore * 10.0) / 10.0;

        double overallAccuracy = totalQuestions > 0
                ? Math.round(((double) totalCorrect / totalQuestions * 100.0) * 10.0) / 10.0
                : 0.0;

        List<Double> recentScores = completedQuizzes.stream()
                .limit(10)
                .map(q -> q.getScore() != null ? q.getScore() : 0.0)
                .collect(Collectors.toList());

        return new PerformanceSummaryResponse(
                totalQuizzes,
                totalQuestions,
                totalCorrect,
                roundedAvgScore,
                overallAccuracy,
                recentScores
        );
    }

    @Transactional(readOnly = true)
    public List<TopicPerformanceResponse> getTopicPerformances(User user) {
        List<QuizAnswer> answers = quizAnswerRepository.findByQuizUserIdAndQuizStatus(user.getId(), QuizStatus.COMPLETED);

        if (answers.isEmpty()) {
            return List.of();
        }

        Map<Topic, List<QuizAnswer>> answersByTopic = answers.stream()
                .collect(Collectors.groupingBy(a -> a.getQuestion().getTopic()));

        List<TopicPerformanceResponse> responses = new ArrayList<>();

        for (Map.Entry<Topic, List<QuizAnswer>> entry : answersByTopic.entrySet()) {
            Topic topic = entry.getKey();
            List<QuizAnswer> topicAnswers = entry.getValue();

            int totalQuestions = topicAnswers.size();
            int correct = (int) topicAnswers.stream().filter(QuizAnswer::isCorrect).count();
            int wrong = totalQuestions - correct;

            double accuracy = Math.round(((double) correct / totalQuestions * 100.0) * 10.0) / 10.0;

            String masteryLevel;
            if (accuracy < 60.0) {
                masteryLevel = "CRITICAL";
            } else if (accuracy < 80.0) {
                masteryLevel = "REGULAR";
            } else {
                masteryLevel = "MASTERED";
            }

            responses.add(new TopicPerformanceResponse(
                    topic.getId(),
                    topic.getName(),
                    topic.getSubject().getId(),
                    topic.getSubject().getName(),
                    totalQuestions,
                    correct,
                    wrong,
                    accuracy,
                    masteryLevel
            ));
        }

        // Ordena assuntos mais criticos (menor aproveitamento) primeiro
        responses.sort(Comparator.comparingDouble(TopicPerformanceResponse::getAccuracyRate)
                .thenComparing(Comparator.comparingInt(TopicPerformanceResponse::getWrongAnswers).reversed()));

        return responses;
    }

    @Transactional(readOnly = true)
    public PerformanceRecommendationResponse getRecommendations(User user) {
        List<TopicPerformanceResponse> allTopics = getTopicPerformances(user);

        // Identifica topicos com dificuldade (CRITICAL ou REGULAR)
        List<TopicPerformanceResponse> criticalTopics = allTopics.stream()
                .filter(t -> !"MASTERED".equals(t.getMasteryLevel()))
                .collect(Collectors.toList());

        List<RecommendedMaterialDto> recommendedMaterials = new ArrayList<>();
        List<String> weakTopicNames = new ArrayList<>();

        List<TopicPerformanceResponse> targetTopics = !criticalTopics.isEmpty() ? criticalTopics : allTopics;

        for (TopicPerformanceResponse topicPerf : targetTopics) {
            weakTopicNames.add(topicPerf.getTopicName());
            List<StudyMaterial> materials = studyMaterialRepository.findByTopicId(topicPerf.getTopicId());
            for (StudyMaterial sm : materials) {
                recommendedMaterials.add(new RecommendedMaterialDto(
                        sm.getId(),
                        sm.getTitle(),
                        sm.getType().name(),
                        topicPerf.getTopicId(),
                        topicPerf.getTopicName()
                ));
            }
        }

        // Chama servico de IA para formular plano de estudos direcionado
        String aiStudyPlan = groqAiService.generateStudyPlan(weakTopicNames);

        return new PerformanceRecommendationResponse(
                criticalTopics,
                recommendedMaterials,
                aiStudyPlan,
                groqAiService.getProviderName()
        );
    }
}
