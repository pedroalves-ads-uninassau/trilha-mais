package com.trilhamais.backend.repository;

import com.trilhamais.backend.model.StudyMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para operacoes com materiais de estudo (StudyMaterial).
 * Assegura isolamento multitenancy navegando topic -> subject -> user.
 */
@Repository
public interface StudyMaterialRepository extends JpaRepository<StudyMaterial, Long> {

    List<StudyMaterial> findByTopicIdAndTopicSubjectUserIdOrderByCreatedAtDesc(Long topicId, Long userId);

    Optional<StudyMaterial> findByIdAndTopicSubjectUserId(Long id, Long userId);

    long countByTopicId(Long topicId);
}
