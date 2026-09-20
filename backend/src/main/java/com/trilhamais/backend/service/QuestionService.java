package com.trilhamais.backend.service;

import com.trilhamais.backend.dto.*;
import com.trilhamais.backend.exception.ResourceNotFoundException;
import com.trilhamais.backend.model.Question;
import com.trilhamais.backend.model.QuestionOption;
import com.trilhamais.backend.model.Topic;
import com.trilhamais.backend.model.User;
import com.trilhamais.backend.repository.QuestionOptionRepository;
import com.trilhamais.backend.repository.QuestionRepository;
import com.trilhamais.backend.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final TopicRepository topicRepository;

    public QuestionService(QuestionRepository questionRepository,
                           QuestionOptionRepository questionOptionRepository,
                           TopicRepository topicRepository) {
        this.questionRepository = questionRepository;
        this.questionOptionRepository = questionOptionRepository;
        this.topicRepository = topicRepository;
    }

    @Transactional
    public QuestionDetailResponse createQuestion(User user, Long topicId, QuestionRequest request) {
        Topic topic = topicRepository.findByIdAndSubjectUserId(topicId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tópico não encontrado com ID: " + topicId));

        validateOptions(request.getOptions());

        Question question = new Question(request.getText().trim(),
                request.getExplanation() != null ? request.getExplanation().trim() : null,
                topic);

        for (OptionRequest optReq : request.getOptions()) {
            QuestionOption opt = new QuestionOption(optReq.getText().trim(), optReq.isCorrect());
            question.addOption(opt);
        }

        Question saved = questionRepository.save(question);
        return mapToDetailResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<QuestionDetailResponse> getQuestionsByTopic(User user, Long topicId) {
        topicRepository.findByIdAndSubjectUserId(topicId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tópico não encontrado com ID: " + topicId));

        List<Question> questions = questionRepository.findByTopicIdAndTopicSubjectUserId(topicId, user.getId());
        return questions.stream()
                .map(this::mapToDetailResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public QuestionDetailResponse getQuestionById(User user, Long id) {
        Question question = questionRepository.findByIdAndTopicSubjectUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Questão não encontrada com ID: " + id));

        return mapToDetailResponse(question);
    }

    @Transactional
    public void deleteQuestion(User user, Long id) {
        Question question = questionRepository.findByIdAndTopicSubjectUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Questão não encontrada com ID: " + id));

        questionRepository.delete(question);
    }

    private void validateOptions(List<OptionRequest> options) {
        if (options == null || options.size() < 2) {
            throw new IllegalArgumentException("A questão deve conter pelo menos 2 alternativas.");
        }
        long correctCount = options.stream().filter(OptionRequest::isCorrect).count();
        if (correctCount != 1) {
            throw new IllegalArgumentException("A questão deve conter exatamente uma alternativa correta.");
        }
    }

    public QuestionDetailResponse mapToDetailResponse(Question question) {
        List<OptionDetailResponse> optionResponses = question.getOptions().stream()
                .map(opt -> new OptionDetailResponse(opt.getId(), opt.getText(), opt.isCorrect()))
                .collect(Collectors.toList());

        return new QuestionDetailResponse(
                question.getId(),
                question.getText(),
                question.getExplanation(),
                question.getTopic().getId(),
                question.getTopic().getName(),
                optionResponses
        );
    }

    public QuestionResponse mapToPublicResponse(Question question) {
        List<OptionResponse> optionResponses = question.getOptions().stream()
                .map(opt -> new OptionResponse(opt.getId(), opt.getText()))
                .collect(Collectors.toList());

        return new QuestionResponse(
                question.getId(),
                question.getText(),
                optionResponses
        );
    }
}
