package com.trilhamais.backend.repository;

import com.trilhamais.backend.model.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {

    List<QuizAnswer> findByQuizId(Long quizId);

    Optional<QuizAnswer> findByQuizIdAndQuestionId(Long quizId, Long questionId);
}
