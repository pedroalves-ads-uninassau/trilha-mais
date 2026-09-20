package com.trilhamais.backend.service;

import com.trilhamais.backend.dto.*;
import com.trilhamais.backend.exception.ResourceNotFoundException;
import com.trilhamais.backend.model.*;
import com.trilhamais.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizAnswerRepository quizAnswerRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final TopicRepository topicRepository;
    private final SubjectRepository subjectRepository;
    private final QuestionService questionService;

    public QuizService(QuizRepository quizRepository,
                       QuizQuestionRepository quizQuestionRepository,
                       QuizAnswerRepository quizAnswerRepository,
                       QuestionRepository questionRepository,
                       QuestionOptionRepository questionOptionRepository,
                       TopicRepository topicRepository,
                       SubjectRepository subjectRepository,
                       QuestionService questionService) {
        this.quizRepository = quizRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.quizAnswerRepository = quizAnswerRepository;
        this.questionRepository = questionRepository;
        this.questionOptionRepository = questionOptionRepository;
        this.topicRepository = topicRepository;
        this.subjectRepository = subjectRepository;
        this.questionService = questionService;
    }

    @Transactional
    public QuizDetailResponse generateQuiz(User user, QuizGenerateRequest request) {
        Topic topic = null;
        Subject subject = null;
        List<Question> candidateQuestions = new ArrayList<>();

        if (request.getTopicId() != null) {
            topic = topicRepository.findByIdAndSubjectUserId(request.getTopicId(), user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tópico não encontrado com ID: " + request.getTopicId()));
            subject = topic.getSubject();
            candidateQuestions = questionRepository.findByTopicIdAndTopicSubjectUserId(topic.getId(), user.getId());
        } else if (request.getSubjectId() != null) {
            subject = subjectRepository.findByIdAndUserId(request.getSubjectId(), user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Matéria não encontrada com ID: " + request.getSubjectId()));
            candidateQuestions = questionRepository.findByTopicSubjectIdAndTopicSubjectUserId(subject.getId(), user.getId());
        } else {
            throw new IllegalArgumentException("É necessário informar ao menos um topicId ou subjectId para gerar o simulado.");
        }

        // Se ainda não houver questões no banco para o assunto/matéria, cria automaticamente questões didáticas padrão
        if (candidateQuestions.isEmpty()) {
            if (topic != null) {
                candidateQuestions = createDefaultQuestionsForTopic(topic);
            } else {
                List<Topic> topics = topicRepository.findBySubjectIdAndSubjectUserId(subject.getId(), user.getId());
                if (topics.isEmpty()) {
                    Topic defaultTopic = topicRepository.save(new Topic("Introdução Geral", "Conteúdo introdutório da matéria", subject));
                    candidateQuestions = createDefaultQuestionsForTopic(defaultTopic);
                } else {
                    candidateQuestions = createDefaultQuestionsForTopic(topics.get(0));
                }
            }
        }

        int desiredCount = (request.getQuestionCount() != null && request.getQuestionCount() > 0)
                ? request.getQuestionCount()
                : 10;
        int actualCount = Math.min(desiredCount, candidateQuestions.size());

        List<Question> selectedQuestions = new ArrayList<>(candidateQuestions);
        Collections.shuffle(selectedQuestions);
        selectedQuestions = selectedQuestions.subList(0, actualCount);

        String title = (request.getTitle() != null && !request.getTitle().isBlank())
                ? request.getTitle().trim()
                : "Simulado: " + (topic != null ? topic.getName() : subject.getName()) + " (" + actualCount + " questões)";

        Quiz quiz = new Quiz(title, user, topic, subject, actualCount);
        Quiz savedQuiz = quizRepository.save(quiz);

        int order = 1;
        for (Question q : selectedQuestions) {
            QuizQuestion qq = new QuizQuestion(savedQuiz, q, order++);
            savedQuiz.addQuizQuestion(qq);
            quizQuestionRepository.save(qq);
        }

        return mapToDetailResponse(savedQuiz);
    }

    @Transactional(readOnly = true)
    public QuizDetailResponse getQuizForExecution(User user, Long quizId) {
        Quiz quiz = quizRepository.findByIdAndUserId(quizId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Simulado não encontrado com ID: " + quizId));

        return mapToDetailResponse(quiz);
    }

    @Transactional
    public QuizResultResponse submitQuiz(User user, Long quizId, QuizSubmitRequest request) {
        Quiz quiz = quizRepository.findByIdAndUserId(quizId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Simulado não encontrado com ID: " + quizId));

        if (quiz.getStatus() == QuizStatus.COMPLETED) {
            throw new IllegalStateException("Este simulado já foi finalizado e corrigido.");
        }

        List<QuizQuestion> quizQuestions = quizQuestionRepository.findByQuizIdOrderByOrderIndexAsc(quiz.getId());
        Map<Long, Question> questionMap = quizQuestions.stream()
                .collect(Collectors.toMap(qq -> qq.getQuestion().getId(), QuizQuestion::getQuestion));

        Map<Long, Long> submissionMap = new HashMap<>();
        if (request.getAnswers() != null) {
            for (AnswerItemRequest item : request.getAnswers()) {
                submissionMap.put(item.getQuestionId(), item.getSelectedOptionId());
            }
        }

        int correctCount = 0;
        List<QuizCorrectionItemResponse> results = new ArrayList<>();

        for (QuizQuestion qq : quizQuestions) {
            Question question = qq.getQuestion();
            Long selectedOptionId = submissionMap.get(question.getId());

            QuestionOption selectedOption = null;
            if (selectedOptionId != null) {
                selectedOption = questionOptionRepository.findByIdAndQuestionId(selectedOptionId, question.getId())
                        .orElse(null);
            }

            QuestionOption correctOption = question.getOptions().stream()
                    .filter(QuestionOption::isCorrect)
                    .findFirst()
                    .orElse(null);

            boolean isCorrect = selectedOption != null && selectedOption.isCorrect();
            if (isCorrect) {
                correctCount++;
            }

            QuizAnswer quizAnswer = new QuizAnswer(quiz, question, selectedOption, isCorrect);
            quiz.addAnswer(quizAnswer);
            quizAnswerRepository.save(quizAnswer);

            results.add(new QuizCorrectionItemResponse(
                    question.getId(),
                    question.getText(),
                    selectedOption != null ? selectedOption.getId() : null,
                    selectedOption != null ? selectedOption.getText() : "Não respondida",
                    correctOption != null ? correctOption.getId() : null,
                    correctOption != null ? correctOption.getText() : "Gabarito indisponível",
                    isCorrect,
                    question.getExplanation()
            ));
        }

        int total = quiz.getTotalQuestions() > 0 ? quiz.getTotalQuestions() : quizQuestions.size();
        double rawScore = ((double) correctCount / total) * 10.0;
        double roundedScore = Math.round(rawScore * 10.0) / 10.0;
        double percentage = Math.round(((double) correctCount / total) * 1000.0) / 10.0;

        quiz.setCorrectAnswers(correctCount);
        quiz.setScore(roundedScore);
        quiz.setStatus(QuizStatus.COMPLETED);
        quiz.setCompletedAt(LocalDateTime.now());
        quizRepository.save(quiz);

        return new QuizResultResponse(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getStatus().name(),
                total,
                correctCount,
                roundedScore,
                percentage,
                quiz.getCompletedAt(),
                results
        );
    }

    @Transactional(readOnly = true)
    public QuizResultResponse getQuizResult(User user, Long quizId) {
        Quiz quiz = quizRepository.findByIdAndUserId(quizId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Simulado não encontrado com ID: " + quizId));

        if (quiz.getStatus() != QuizStatus.COMPLETED) {
            throw new IllegalStateException("O simulado ainda não foi concluído. Submeta as respostas para ver o resultado.");
        }

        List<QuizQuestion> quizQuestions = quizQuestionRepository.findByQuizIdOrderByOrderIndexAsc(quiz.getId());
        List<QuizAnswer> answers = quizAnswerRepository.findByQuizId(quiz.getId());
        Map<Long, QuizAnswer> answerMap = answers.stream()
                .collect(Collectors.toMap(a -> a.getQuestion().getId(), a -> a, (a1, a2) -> a1));

        List<QuizCorrectionItemResponse> results = new ArrayList<>();
        for (QuizQuestion qq : quizQuestions) {
            Question question = qq.getQuestion();
            QuizAnswer answer = answerMap.get(question.getId());

            QuestionOption correctOption = question.getOptions().stream()
                    .filter(QuestionOption::isCorrect)
                    .findFirst()
                    .orElse(null);

            QuestionOption selectedOption = answer != null ? answer.getSelectedOption() : null;
            boolean isCorrect = answer != null && answer.isCorrect();

            results.add(new QuizCorrectionItemResponse(
                    question.getId(),
                    question.getText(),
                    selectedOption != null ? selectedOption.getId() : null,
                    selectedOption != null ? selectedOption.getText() : "Não respondida",
                    correctOption != null ? correctOption.getId() : null,
                    correctOption != null ? correctOption.getText() : "Gabarito indisponível",
                    isCorrect,
                    question.getExplanation()
            ));
        }

        int total = quiz.getTotalQuestions();
        int correctCount = quiz.getCorrectAnswers() != null ? quiz.getCorrectAnswers() : 0;
        double score = quiz.getScore() != null ? quiz.getScore() : 0.0;
        double percentage = total > 0 ? Math.round(((double) correctCount / total) * 1000.0) / 10.0 : 0.0;

        return new QuizResultResponse(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getStatus().name(),
                total,
                correctCount,
                score,
                percentage,
                quiz.getCompletedAt(),
                results
        );
    }

    @Transactional(readOnly = true)
    public List<QuizResponse> listUserQuizzes(User user) {
        List<Quiz> quizzes = quizRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return quizzes.stream()
                .map(this::mapToSummaryResponse)
                .collect(Collectors.toList());
    }

    private QuizDetailResponse mapToDetailResponse(Quiz quiz) {
        List<QuizQuestion> quizQuestions = quizQuestionRepository.findByQuizIdOrderByOrderIndexAsc(quiz.getId());
        List<QuestionResponse> questionResponses = quizQuestions.stream()
                .map(qq -> questionService.mapToPublicResponse(qq.getQuestion()))
                .collect(Collectors.toList());

        return new QuizDetailResponse(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getTopic() != null ? quiz.getTopic().getId() : null,
                quiz.getTopic() != null ? quiz.getTopic().getName() : null,
                quiz.getSubject() != null ? quiz.getSubject().getId() : null,
                quiz.getSubject() != null ? quiz.getSubject().getName() : null,
                quiz.getTotalQuestions(),
                quiz.getStatus().name(),
                quiz.getCreatedAt(),
                questionResponses
        );
    }

    private QuizResponse mapToSummaryResponse(Quiz quiz) {
        return new QuizResponse(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getTopic() != null ? quiz.getTopic().getId() : null,
                quiz.getTopic() != null ? quiz.getTopic().getName() : null,
                quiz.getSubject() != null ? quiz.getSubject().getId() : null,
                quiz.getSubject() != null ? quiz.getSubject().getName() : null,
                quiz.getTotalQuestions(),
                quiz.getCorrectAnswers(),
                quiz.getScore(),
                quiz.getStatus().name(),
                quiz.getCreatedAt(),
                quiz.getCompletedAt()
        );
    }

    private List<Question> createDefaultQuestionsForTopic(Topic topic) {
        List<Question> created = new ArrayList<>();
        String topicName = topic.getName();

        Question q1 = new Question(
                "Qual dos conceitos a seguir melhor define o foco principal do estudo de " + topicName + "?",
                "O estudo de " + topicName + " concentra-se na compreensão de fundamentos, modelos teóricos e aplicação prática estruturada.",
                topic
        );
        q1.addOption(new QuestionOption("Compreensão dos fundamentos, princípios e aplicações práticas estruturadas.", true));
        q1.addOption(new QuestionOption("Apenas memorização de dados históricos sem aplicação prática.", false));
        q1.addOption(new QuestionOption("Substituição completa de raciocínio lógico por suposições empíricas.", false));
        q1.addOption(new QuestionOption("Abordagem superficial sem critérios metodológicos definidos.", false));
        created.add(questionRepository.save(q1));

        Question q2 = new Question(
                "Em relação às boas práticas aplicadas a " + topicName + ", assinale a alternativa correta:",
                "A adoção de padrões consolidados e organização contínua garante maior qualidade e retenção do aprendizado.",
                topic
        );
        q2.addOption(new QuestionOption("Adoção de padrões sistemáticos, validação consistente e revisão contínua.", true));
        q2.addOption(new QuestionOption("Ignorar diretrizes e trabalhar de maneira desordenada.", false));
        q2.addOption(new QuestionOption("Não realizar qualquer tipo de revisão ou teste de conhecimento.", false));
        q2.addOption(new QuestionOption("Tratar todas as hipóteses como verdades absolutas sem validação.", false));
        created.add(questionRepository.save(q2));

        return created;
    }
}
