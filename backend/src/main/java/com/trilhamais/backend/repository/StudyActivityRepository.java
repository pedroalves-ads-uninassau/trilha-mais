package com.trilhamais.backend.repository;

import com.trilhamais.backend.model.ActivityType;
import com.trilhamais.backend.model.StudyActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StudyActivityRepository extends JpaRepository<StudyActivity, Long> {

    boolean existsByUserIdAndActivityDateAndActivityType(Long userId, LocalDate activityDate, ActivityType activityType);

    // Dias distintos em que o estudante estudou, do mais recente para o mais antigo
    @Query("SELECT DISTINCT a.activityDate FROM StudyActivity a WHERE a.user.id = :userId ORDER BY a.activityDate DESC")
    List<LocalDate> findDistinctActivityDatesByUserId(@Param("userId") Long userId);
}
