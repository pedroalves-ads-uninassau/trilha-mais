package com.trilhamais.backend.repository;

import com.trilhamais.backend.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para operacoes de persistencia da entidade Topic.
 * Realiza consultas isoladas pelo usuario dono da materia correspondente.
 */
@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    List<Topic> findBySubjectIdAndSubjectUserIdOrderByNameAsc(Long subjectId, Long userId);

    List<Topic> findBySubjectIdAndSubjectUserId(Long subjectId, Long userId);

    Optional<Topic> findByIdAndSubjectUserId(Long id, Long userId);

    long countBySubjectId(Long subjectId);
}
