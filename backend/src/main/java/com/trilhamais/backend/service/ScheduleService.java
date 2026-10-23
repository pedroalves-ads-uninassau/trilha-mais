package com.trilhamais.backend.service;

import com.trilhamais.backend.dto.ScheduleRequest;
import com.trilhamais.backend.dto.ScheduleResponse;
import com.trilhamais.backend.exception.ResourceNotFoundException;
import com.trilhamais.backend.model.ActivityType;
import com.trilhamais.backend.model.Schedule;
import com.trilhamais.backend.model.Subject;
import com.trilhamais.backend.model.Topic;
import com.trilhamais.backend.model.User;
import com.trilhamais.backend.repository.ScheduleRepository;
import com.trilhamais.backend.repository.SubjectRepository;
import com.trilhamais.backend.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");

    private final ScheduleRepository scheduleRepository;
    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;
    private final StudyActivityService studyActivityService;

    public ScheduleService(ScheduleRepository scheduleRepository, SubjectRepository subjectRepository,
                           TopicRepository topicRepository, StudyActivityService studyActivityService) {
        this.scheduleRepository = scheduleRepository;
        this.subjectRepository = subjectRepository;
        this.topicRepository = topicRepository;
        this.studyActivityService = studyActivityService;
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponse> getSchedulesByUser(User user) {
        return scheduleRepository.findByUserOrderByDaysAheadAscScheduledTimeAsc(user)
                .stream()
                .sorted(Comparator.comparing(this::effectiveDate)
                        .thenComparing(s -> s.getScheduledTime() != null ? s.getScheduledTime() : ""))
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
        applyRequest(user, schedule, request);
        Schedule saved = scheduleRepository.save(schedule);
        return toResponse(saved);
    }

    /**
     * Edita um compromisso da agenda (titulo, data, horario e materia/assunto).
     */
    @Transactional
    public ScheduleResponse updateSchedule(User user, Long id, ScheduleRequest request) {
        Schedule schedule = findOwned(user, id);
        schedule.setTitle(request.getTitle());
        schedule.setSubjectName(request.getSubjectName());
        schedule.setColorKey(request.getColorKey());
        schedule.setIcon(request.getIcon());
        schedule.setScheduledTime(request.getScheduledTime());
        applyRequest(user, schedule, request);
        return toResponse(scheduleRepository.save(schedule));
    }

    @Transactional
    public ScheduleResponse toggleCompleted(User user, Long id) {
        Schedule schedule = findOwned(user, id);

        schedule.setCompleted(!schedule.isCompleted());
        if (schedule.isCompleted()) {
            studyActivityService.registerActivity(user, ActivityType.SCHEDULE);
        }
        return toResponse(scheduleRepository.save(schedule));
    }

    @Transactional
    public void deleteSchedule(User user, Long id) {
        Schedule schedule = findOwned(user, id);
        scheduleRepository.delete(schedule);
    }

    private Schedule findOwned(User user, Long id) {
        return scheduleRepository.findById(id)
                .filter(s -> s.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Compromisso de agenda não encontrado."));
    }

    /**
     * Grava a data real do compromisso (hoje + daysAhead quando a data nao vem) e liga a materia/assunto.
     * Quando ha materia ou assunto, nome, cor e icone sao copiados deles se o app nao enviar.
     */
    private void applyRequest(User user, Schedule schedule, ScheduleRequest request) {
        LocalDate date = request.getScheduledDate() != null
                ? request.getScheduledDate()
                : LocalDate.now(ZONE).plusDays(request.getDaysAhead());
        schedule.setScheduledDate(date);

        Topic topic = null;
        Subject subject = null;
        if (request.getTopicId() != null) {
            topic = topicRepository.findByIdAndSubjectUserId(request.getTopicId(), user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assunto não encontrado com o id: " + request.getTopicId()));
            subject = topic.getSubject();
        }
        if (request.getSubjectId() != null) {
            subject = subjectRepository.findByIdAndUserId(request.getSubjectId(), user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Matéria não encontrada com o id: " + request.getSubjectId()));
        }
        schedule.setTopic(topic);
        schedule.setSubject(subject);

        if (subject != null) {
            if (schedule.getSubjectName() == null || schedule.getSubjectName().isBlank()) {
                schedule.setSubjectName(subject.getName());
            }
            if (schedule.getColorKey() == null || schedule.getColorKey().isBlank()) {
                schedule.setColorKey(subject.getColorKey());
            }
            if (schedule.getIcon() == null || schedule.getIcon().isBlank()) {
                schedule.setIcon(subject.getIcon());
            }
        }
    }

    // Compromissos antigos (sem data gravada) usam a data de criacao + daysAhead
    private LocalDate effectiveDate(Schedule s) {
        if (s.getScheduledDate() != null) {
            return s.getScheduledDate();
        }
        LocalDate base = s.getCreatedAt() != null ? s.getCreatedAt().toLocalDate() : LocalDate.now(ZONE);
        return base.plusDays(s.getDaysAhead());
    }

    private ScheduleResponse toResponse(Schedule s) {
        LocalDate date = effectiveDate(s);
        // daysAhead e sempre recalculado em relacao a hoje (0 = Hoje, 1 = Amanha, negativo = atrasado)
        int daysAhead = (int) ChronoUnit.DAYS.between(LocalDate.now(ZONE), date);

        ScheduleResponse response = new ScheduleResponse(
                s.getId(),
                s.getTitle(),
                s.getSubjectName(),
                s.getColorKey(),
                s.getIcon(),
                daysAhead,
                s.getScheduledTime(),
                s.isCompleted()
        );
        response.setScheduledDate(date);
        response.setSubjectId(s.getSubject() != null ? s.getSubject().getId() : null);
        response.setTopicId(s.getTopic() != null ? s.getTopic().getId() : null);
        return response;
    }
}
