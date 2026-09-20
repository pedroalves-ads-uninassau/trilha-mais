package com.trilhamais.backend.service;

import com.trilhamais.backend.dto.TopicRequest;
import com.trilhamais.backend.dto.TopicResponse;
import com.trilhamais.backend.exception.ResourceNotFoundException;
import com.trilhamais.backend.model.Subject;
import com.trilhamais.backend.model.Topic;
import com.trilhamais.backend.model.User;
import com.trilhamais.backend.repository.SubjectRepository;
import com.trilhamais.backend.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Camada de servico para gestao de Assuntos (Topic) de uma Materia de estudo.
 */
@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final SubjectRepository subjectRepository;

    public TopicService(TopicRepository topicRepository, SubjectRepository subjectRepository) {
        this.topicRepository = topicRepository;
        this.subjectRepository = subjectRepository;
    }

    /**
     * Cria um novo assunto vinculado a uma materia pertencente ao estudante autenticado.
     */
    @Transactional
    public TopicResponse createTopic(Long subjectId, TopicRequest request, User user) {
        Subject subject = subjectRepository.findByIdAndUserId(subjectId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Matéria não encontrada com o id: " + subjectId));

        Topic topic = new Topic(request.getName().trim(), request.getDescription(), subject);
        Topic saved = topicRepository.save(topic);

        return toTopicResponse(saved);
    }

    /**
     * Retorna todos os assuntos de uma materia especifica do estudante.
     */
    @Transactional(readOnly = true)
    public List<TopicResponse> getTopicsBySubject(Long subjectId, User user) {
        // Valida se a materia existe e pertence ao usuario
        subjectRepository.findByIdAndUserId(subjectId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Matéria não encontrada com o id: " + subjectId));

        return topicRepository.findBySubjectIdAndSubjectUserIdOrderByNameAsc(subjectId, user.getId())
                .stream()
                .map(this::toTopicResponse)
                .toList();
    }

    /**
     * Retorna os detalhes de um assunto especifico do estudante.
     */
    @Transactional(readOnly = true)
    public TopicResponse getTopicById(Long topicId, User user) {
        Topic topic = topicRepository.findByIdAndSubjectUserId(topicId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Assunto não encontrado com o id: " + topicId));

        return toTopicResponse(topic);
    }

    /**
     * Atualiza dados de um assunto pertencente ao estudante.
     */
    @Transactional
    public TopicResponse updateTopic(Long topicId, TopicRequest request, User user) {
        Topic topic = topicRepository.findByIdAndSubjectUserId(topicId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Assunto não encontrado com o id: " + topicId));

        topic.setName(request.getName().trim());
        topic.setDescription(request.getDescription());
        Topic updated = topicRepository.save(topic);

        return toTopicResponse(updated);
    }

    /**
     * Remove um assunto especifico.
     */
    @Transactional
    public void deleteTopic(Long topicId, User user) {
        Topic topic = topicRepository.findByIdAndSubjectUserId(topicId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Assunto não encontrado com o id: " + topicId));

        topicRepository.delete(topic);
    }

    private TopicResponse toTopicResponse(Topic topic) {
        return new TopicResponse(
                topic.getId(),
                topic.getName(),
                topic.getDescription(),
                topic.getSubject().getId(),
                topic.getSubject().getName(),
                topic.getCreatedAt()
        );
    }
}
