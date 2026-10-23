package com.trilhamais.backend.service;

import com.trilhamais.backend.model.ActivityType;
import com.trilhamais.backend.model.StudyActivity;
import com.trilhamais.backend.model.User;
import com.trilhamais.backend.repository.StudyActivityRepository;
import com.trilhamais.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * Servico que registra os dias de estudo e calcula a sequencia de dias seguidos (streak).
 * A sequencia conta ate hoje; se o estudante ainda nao estudou hoje, conta ate ontem
 * (assim ela so "zera" quando ele passa um dia inteiro sem estudar).
 */
@Service
public class StudyActivityService {

    // Fuso do Brasil para que a virada do dia siga o horario do estudante, nao o do servidor
    private static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");

    private final StudyActivityRepository studyActivityRepository;
    private final UserRepository userRepository;

    public StudyActivityService(StudyActivityRepository studyActivityRepository, UserRepository userRepository) {
        this.studyActivityRepository = studyActivityRepository;
        this.userRepository = userRepository;
    }

    /**
     * Registra que o estudante estudou hoje (uma vez por tipo de atividade) e atualiza a sequencia salva.
     */
    @Transactional
    public void registerActivity(User user, ActivityType type) {
        LocalDate today = LocalDate.now(ZONE);
        if (!studyActivityRepository.existsByUserIdAndActivityDateAndActivityType(user.getId(), today, type)) {
            studyActivityRepository.save(new StudyActivity(user, today, type));
        }

        int streak = getCurrentStreak(user);
        userRepository.findById(user.getId()).ifPresent(u -> {
            if (u.getStreakDays() != streak) {
                u.setStreakDays(streak);
                userRepository.save(u);
            }
        });
        user.setStreakDays(streak);
    }

    /**
     * Calcula a sequencia atual de dias seguidos de estudo a partir do historico.
     */
    @Transactional(readOnly = true)
    public int getCurrentStreak(User user) {
        List<LocalDate> dates = studyActivityRepository.findDistinctActivityDatesByUserId(user.getId());
        if (dates.isEmpty()) {
            return 0;
        }

        LocalDate today = LocalDate.now(ZONE);
        LocalDate expected = dates.get(0).equals(today) ? today : today.minusDays(1);

        int streak = 0;
        for (LocalDate date : dates) {
            if (date.equals(expected)) {
                streak++;
                expected = expected.minusDays(1);
            } else if (date.isBefore(expected)) {
                break;
            }
        }
        return streak;
    }
}
