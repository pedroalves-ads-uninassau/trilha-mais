package com.trilhamais.backend.service;

import com.trilhamais.backend.dto.SubjectDetailResponse;
import com.trilhamais.backend.dto.SubjectRequest;
import com.trilhamais.backend.dto.SubjectResponse;
import com.trilhamais.backend.dto.TopicResponse;
import com.trilhamais.backend.exception.ResourceNotFoundException;
import com.trilhamais.backend.model.Subject;
import com.trilhamais.backend.model.User;
import com.trilhamais.backend.repository.SubjectRepository;
import com.trilhamais.backend.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Camada de servico para gestao de Materias de estudo (Subject).
 * Aplica regras de negocio e garante isolamento por estudante autenticado.
 */
@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;

    public SubjectService(SubjectRepository subjectRepository, TopicRepository topicRepository) {
        this.subjectRepository = subjectRepository;
        this.topicRepository = topicRepository;
    }

    /**
     * Cria uma nova materia vinculada ao estudante autenticado.
     */
    @Transactional
    public SubjectResponse createSubject(SubjectRequest request, User user) {
        String trimmedName = request.getName().trim();

        if (subjectRepository.existsByNameIgnoreCaseAndUserId(trimmedName, user.getId())) {
            throw new IllegalArgumentException("Você já possui uma matéria cadastrada com o nome: " + trimmedName);
        }

        Subject subject = new Subject(trimmedName, request.getDescription(), user);
        Subject saved = subjectRepository.save(subject);

        return toSubjectResponse(saved, 0);
    }

    /**
     * Retorna todas as materias cadastradas pelo estudante autenticado.
     */
    @Transactional(readOnly = true)
    public List<SubjectResponse> getAllSubjects(User user) {
        List<Subject> subjects = subjectRepository.findByUserIdOrderByNameAsc(user.getId());

        return subjects.stream()
                .map(s -> {
                    int count = (int) topicRepository.countBySubjectId(s.getId());
                    return toSubjectResponse(s, count);
                })
                .toList();
    }

    /**
     * Retorna os detalhes de uma materia especifica do estudante, incluindo a lista de assuntos.
     */
    @Transactional(readOnly = true)
    public SubjectDetailResponse getSubjectById(Long id, User user) {
        Subject subject = subjectRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Matéria não encontrada com o id: " + id));

        List<TopicResponse> topics = topicRepository.findBySubjectIdAndSubjectUserIdOrderByNameAsc(id, user.getId())
                .stream()
                .map(t -> new TopicResponse(
                        t.getId(),
                        t.getName(),
                        t.getDescription(),
                        subject.getId(),
                        subject.getName(),
                        t.getCreatedAt()
                ))
                .toList();

        return new SubjectDetailResponse(
                subject.getId(),
                subject.getName(),
                subject.getDescription(),
                topics,
                subject.getCreatedAt()
        );
    }

    /**
     * Atualiza dados de uma materia do estudante autenticado.
     */
    @Transactional
    public SubjectResponse updateSubject(Long id, SubjectRequest request, User user) {
        Subject subject = subjectRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Matéria não encontrada com o id: " + id));

        String trimmedName = request.getName().trim();

        if (subjectRepository.existsByNameIgnoreCaseAndUserIdAndIdNot(trimmedName, user.getId(), id)) {
            throw new IllegalArgumentException("Você já possui outra matéria cadastrada com o nome: " + trimmedName);
        }

        subject.setName(trimmedName);
        subject.setDescription(request.getDescription());
        Subject updated = subjectRepository.save(subject);

        int count = (int) topicRepository.countBySubjectId(updated.getId());
        return toSubjectResponse(updated, count);
    }

    /**
     * Remove uma materia e todos os seus assuntos associados (cascata).
     */
    @Transactional
    public void deleteSubject(Long id, User user) {
        Subject subject = subjectRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Matéria não encontrada com o id: " + id));

        subjectRepository.delete(subject);
    }

    private SubjectResponse toSubjectResponse(Subject subject, int topicsCount) {
        return new SubjectResponse(
                subject.getId(),
                subject.getName(),
                subject.getDescription(),
                topicsCount,
                subject.getCreatedAt()
        );
    }
}
