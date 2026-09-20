package com.trilhamais.backend.repository;

import com.trilhamais.backend.model.Quiz;
import com.trilhamais.backend.model.QuizStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Quiz> findByIdAndUserId(Long id, Long userId);

    List<Quiz> findByUserIdAndStatusOrderByCompletedAtDesc(Long userId, QuizStatus status);
}
