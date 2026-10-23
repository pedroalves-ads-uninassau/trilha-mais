package com.trilhamais.backend.repository;

import com.trilhamais.backend.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByUserIdOrderByCreatedAtAscIdAsc(Long userId);

    List<ChatMessage> findByUserIdAndSubjectIdOrderByCreatedAtAscIdAsc(Long userId, Long subjectId);

    @Modifying
    @Query("DELETE FROM ChatMessage m WHERE m.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM ChatMessage m WHERE m.user.id = :userId AND m.subject.id = :subjectId")
    void deleteByUserIdAndSubjectId(@Param("userId") Long userId, @Param("subjectId") Long subjectId);
}
