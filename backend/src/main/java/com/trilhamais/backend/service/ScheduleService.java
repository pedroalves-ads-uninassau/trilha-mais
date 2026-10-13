package com.trilhamais.backend.service;

import com.trilhamais.backend.dto.ScheduleRequest;
import com.trilhamais.backend.dto.ScheduleResponse;
import com.trilhamais.backend.exception.ResourceNotFoundException;
import com.trilhamais.backend.model.Schedule;
import com.trilhamais.backend.model.User;
import com.trilhamais.backend.repository.ScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponse> getSchedulesByUser(User user) {
        return scheduleRepository.findByUserOrderByDaysAheadAscScheduledTimeAsc(user)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ScheduleResponse createSchedule(User user, ScheduleRequest request) {
        Schedule schedule = new Schedule(
                request.getTitle(),
                request.getSubjectName(),
                request.getColorKey(),
                request.getIcon(),
                null,
                request.getDaysAhead(),
                request.getScheduledTime(),
                user
        );
        Schedule saved = scheduleRepository.save(schedule);
        return toResponse(saved);
    }

    @Transactional
    public ScheduleResponse toggleCompleted(User user, Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .filter(s -> s.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Compromisso de agenda não encontrado."));

        schedule.setCompleted(!schedule.isCompleted());
        return toResponse(scheduleRepository.save(schedule));
    }

    @Transactional
    public void deleteSchedule(User user, Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .filter(s -> s.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Compromisso de agenda não encontrado."));
        scheduleRepository.delete(schedule);
    }

    private ScheduleResponse toResponse(Schedule s) {
        return new ScheduleResponse(
                s.getId(),
                s.getTitle(),
                s.getSubjectName(),
                s.getColorKey(),
                s.getIcon(),
                s.getDaysAhead(),
                s.getScheduledTime(),
                s.isCompleted()
        );
    }
}
