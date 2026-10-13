package com.trilhamais.backend.repository;

import com.trilhamais.backend.model.Schedule;
import com.trilhamais.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByUserOrderByDaysAheadAscScheduledTimeAsc(User user);
    List<Schedule> findByUserAndCompleted(User user, boolean completed);
}
