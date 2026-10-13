package com.trilhamais.backend.service;

import com.trilhamais.backend.dto.SubjectDetailResponse;
import com.trilhamais.backend.dto.SubjectRequest;
import com.trilhamais.backend.dto.SubjectResponse;
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
 * Camada de servico para gestao de Materias de estudo (Subject).
 * Aplica regras de negocio, calcula progresso visual e garante isolamento por estudante.
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
     * Cria uma nova materia vinculada ao estudante autenticado com seus tokens de design (cor e ícone).
     */
    @Transactional
    public SubjectResponse createSubject(SubjectRequest request, User user) {
        String trimmedName = request.getName().trim();

        if (subjectRepository.existsByNameIgnoreCaseAndUserId(trimmedName, user.getId())) {
            throw new IllegalArgumentException("Você já possui uma matéria cadastrada com o nome: " + trimmedName);
        }

        Subject subject = new Subject(
                trimmedName,
                request.getDescription(),
                request.getColorKey() != null ? request.getColorKey() : "portugues",
                request.getPrimaryColor() != null ? request.getPrimaryColor() : "#2563EB",
                request.getIcon() != null ? request.getIcon() : "📘",
                user
        );
        Subject saved = subjectRepository.save(subject);

        return toSubjectResponse(saved);
    }

    /**
     * Retorna todas as materias cadastradas pelo estudante autenticado com métricas calculadas.
     */
    @Transactional(readOnly = true)
    public List<SubjectResponse> getAllSubjects(User user) {
        List<Subject> subjects = subjectRepository.findByUserIdOrderByNameAsc(user.getId());
        return subjects.stream().map(this::toSubjectResponse).toList();
    }

    /**
     * Retorna os detalhes de uma materia especifica do estudante, incluindo a lista de assuntos e progresso.
     */
    @Transactional(readOnly = true)
    public SubjectDetailResponse getSubjectById(Long id, User user) {
        Subject subject = subjectRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Matéria não encontrada com o id: " + id));

        List<Topic> topicsList = topicRepository.findBySubjectIdAndSubjectUserIdOrderByNameAsc(id, user.getId());

        int total = topicsList.size();
        int completed = (int) topicsList.stream().filter(t -> "concluido".equalsIgnoreCase(t.getStatus())).count();
        int inProgress = (int) topicsList.stream().filter(t -> "andamento".equalsIgnoreCase(t.getStatus())).count();
        int pending = total - completed - inProgress;
        int progressPercent = total > 0 ? Math.round(((float) completed / total) * 100) : 0;

        List<TopicResponse> topics = topicsList.stream()
                .map(t -> new TopicResponse(
                        t.getId(),
                        t.getName(),
                        t.getDescription(),
                        t.getStatus(),
                        subject.getId(),
                        subject.getName(),
                        t.getCreatedAt()
                ))
                .toList();

        return new SubjectDetailResponse(
                subject.getId(),
                subject.getName(),
                subject.getDescription(),
                subject.getColorKey(),
                subject.getPrimaryColor(),
                subject.getIcon(),
                total,
                completed,
                inProgress,
                pending,
                progressPercent,
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
        if (request.getColorKey() != null) subject.setColorKey(request.getColorKey());
        if (request.getPrimaryColor() != null) subject.setPrimaryColor(request.getPrimaryColor());
        if (request.getIcon() != null) subject.setIcon(request.getIcon());

        Subject updated = subjectRepository.save(subject);
        return toSubjectResponse(updated);
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

    private SubjectResponse toSubjectResponse(Subject subject) {
        List<Topic> topics = topicRepository.findBySubjectId(subject.getId());
        int total = topics.size();
        int completed = (int) topics.stream().filter(t -> "concluido".equalsIgnoreCase(t.getStatus())).count();
        int inProgress = (int) topics.stream().filter(t -> "andamento".equalsIgnoreCase(t.getStatus())).count();
        int pending = total - completed - inProgress;
        int progressPercent = total > 0 ? Math.round(((float) completed / total) * 100) : 0;

        return new SubjectResponse(
                subject.getId(),
                subject.getName(),
                subject.getDescription(),
                subject.getColorKey() != null ? subject.getColorKey() : "portugues",
                subject.getPrimaryColor() != null ? subject.getPrimaryColor() : "#2563EB",
                subject.getIcon() != null ? subject.getIcon() : "📘",
                total,
                completed,
                inProgress,
                pending,
                progressPercent,
                subject.getCreatedAt()
        );
    }
}
