package com.trilhamais.backend.service;

import com.trilhamais.backend.dto.StudyMaterialRequest;
import com.trilhamais.backend.dto.StudyMaterialResponse;
import com.trilhamais.backend.exception.ResourceNotFoundException;
import com.trilhamais.backend.model.StudyMaterial;
import com.trilhamais.backend.model.Topic;
import com.trilhamais.backend.model.User;
import com.trilhamais.backend.repository.StudyMaterialRepository;
import com.trilhamais.backend.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Camada de servico para gestao de materiais didaticos (resumos, anotacoes, textos).
 */
@Service
public class StudyMaterialService {

    private final StudyMaterialRepository studyMaterialRepository;
    private final TopicRepository topicRepository;

    public StudyMaterialService(StudyMaterialRepository studyMaterialRepository, TopicRepository topicRepository) {
        this.studyMaterialRepository = studyMaterialRepository;
        this.topicRepository = topicRepository;
    }

    /**
     * Cria um novo material de estudo vinculado a um assunto do estudante autenticado.
     */
    @Transactional
    public StudyMaterialResponse createMaterial(Long topicId, StudyMaterialRequest request, User user) {
        Topic topic = topicRepository.findByIdAndSubjectUserId(topicId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Assunto não encontrado com o id: " + topicId));

        StudyMaterial material = new StudyMaterial(
                request.getTitle().trim(),
                request.getContent().trim(),
                request.getType(),
                topic
        );

        StudyMaterial saved = studyMaterialRepository.save(material);
        return toResponse(saved);
    }

    /**
     * Lista todos os materiais de um assunto pertencente ao estudante.
     */
    @Transactional(readOnly = true)
    public List<StudyMaterialResponse> getMaterialsByTopic(Long topicId, User user) {
        topicRepository.findByIdAndSubjectUserId(topicId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Assunto não encontrado com o id: " + topicId));

        return studyMaterialRepository.findByTopicIdAndTopicSubjectUserIdOrderByCreatedAtDesc(topicId, user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Retorna os detalhes de um material especifico do estudante.
     */
    @Transactional(readOnly = true)
    public StudyMaterialResponse getMaterialById(Long id, User user) {
        StudyMaterial material = studyMaterialRepository.findByIdAndTopicSubjectUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Material não encontrado com o id: " + id));

        return toResponse(material);
    }

    /**
     * Atualiza dados de um material existente.
     */
    @Transactional
    public StudyMaterialResponse updateMaterial(Long id, StudyMaterialRequest request, User user) {
        StudyMaterial material = studyMaterialRepository.findByIdAndTopicSubjectUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Material não encontrado com o id: " + id));

        material.setTitle(request.getTitle().trim());
        material.setContent(request.getContent().trim());
        if (request.getType() != null) {
            material.setType(request.getType());
        }

        StudyMaterial updated = studyMaterialRepository.save(material);
        return toResponse(updated);
    }

    /**
     * Exclui um material de estudo.
     */
    @Transactional
    public void deleteMaterial(Long id, User user) {
        StudyMaterial material = studyMaterialRepository.findByIdAndTopicSubjectUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Material não encontrado com o id: " + id));

        studyMaterialRepository.delete(material);
    }

    private StudyMaterialResponse toResponse(StudyMaterial m) {
        return new StudyMaterialResponse(
                m.getId(),
                m.getTitle(),
                m.getContent(),
                m.getType(),
                m.getTopic().getId(),
                m.getTopic().getName(),
                m.getCreatedAt()
        );
    }
}
