package com.trilhamais.backend.repository;

import com.trilhamais.backend.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByTopicId(Long topicId);

    List<Question> findByTopicIdAndTopicSubjectUserId(Long topicId, Long userId);

    Optional<Question> findByIdAndTopicSubjectUserId(Long id, Long userId);

    List<Question> findByTopicSubjectIdAndTopicSubjectUserId(Long subjectId, Long userId);

    long countByTopicIdAndTopicSubjectUserId(Long topicId, Long userId);
}
