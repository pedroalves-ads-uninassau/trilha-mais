package com.trilhamais.backend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.time.LocalDate;

/**
 * Entidade JPA que registra em quais dias o estudante estudou.
 * E a base do calculo da sequencia de dias seguidos (streak) exibida na Home, Perfil e Progresso.
 * Cada tipo de atividade e registrado no maximo uma vez por dia.
 */
@Entity
@Table(name = "study_activities",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "activity_date", "activity_type"}))
public class StudyActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(name = "activity_date", nullable = false)
    private LocalDate activityDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false, length = 30)
    private ActivityType activityType;

    public StudyActivity() {
    }

    public StudyActivity(User user, LocalDate activityDate, ActivityType activityType) {
        this.user = user;
        this.activityDate = activityDate;
        this.activityType = activityType;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public LocalDate getActivityDate() {
        return activityDate;
    }

    public ActivityType getActivityType() {
        return activityType;
    }
}
