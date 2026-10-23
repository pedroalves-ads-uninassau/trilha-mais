package com.trilhamais.backend.integration;

import com.trilhamais.backend.model.*;
import com.trilhamais.backend.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes do banco de dados (PostgreSQL): confere se o Hibernate criou todas as tabelas da modelagem
 * e se as regras de integridade (UNIQUE, FOREIGN KEY, CHECK e ON DELETE) funcionam direto no banco.
 */
@SpringBootTest
class DatabaseSchemaTest {

    private static final List<String> EXPECTED_TABLES = List.of(
            "users", "subjects", "topics", "study_materials", "questions", "question_options",
            "quizzes", "quiz_questions", "quiz_answers", "schedules",
            "study_activities", "password_reset_tokens", "user_settings", "chat_messages");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private StudyMaterialRepository studyMaterialRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionOptionRepository questionOptionRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private StudyActivityRepository studyActivityRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @BeforeEach
    void setUp() {
        cleanDatabase();
    }

    @AfterEach
    void tearDown() {
        cleanDatabase();
    }

    private void cleanDatabase() {
        scheduleRepository.deleteAll();
        chatMessageRepository.deleteAll();
        studyActivityRepository.deleteAll();
        quizAnswerRepository.deleteAll();
        quizQuestionRepository.deleteAll();
        quizRepository.deleteAll();
        questionOptionRepository.deleteAll();
        questionRepository.deleteAll();
        studyMaterialRepository.deleteAll();
        topicRepository.deleteAll();
        subjectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Banco: todas as 14 tabelas da modelagem existem")
    void shouldHaveAllTablesFromDataModel() {
        List<String> tables = jdbcTemplate.queryForList(
                "SELECT LOWER(table_name) FROM information_schema.tables WHERE LOWER(table_schema) = 'public'",
                String.class);

        for (String table : EXPECTED_TABLES) {
            assertTrue(tables.contains(table), "Tabela ausente no banco: " + table);
        }
    }

    @Test
    @DisplayName("Banco: e-mail de usuário é único (UNIQUE)")
    void shouldRejectDuplicateEmail() {
        userRepository.saveAndFlush(new User("Aluno 1", "repetido@teste.com", "hash"));

        assertThrows(DataIntegrityViolationException.class,
                () -> userRepository.saveAndFlush(new User("Aluno 2", "repetido@teste.com", "hash")));
    }

    @Test
    @DisplayName("Banco: matéria não pode apontar para usuário inexistente (FOREIGN KEY)")
    void shouldRejectSubjectWithoutUser() {
        assertThrows(DataIntegrityViolationException.class, () -> jdbcTemplate.update(
                "INSERT INTO subjects (name, user_id, created_at) VALUES ('Órfã', 999999, CURRENT_TIMESTAMP)"));
    }

    @Test
    @DisplayName("Banco: tipo de atividade fora da lista é recusado (CHECK)")
    void shouldRejectInvalidActivityType() {
        User user = userRepository.save(new User("Aluno", "check@teste.com", "hash"));

        assertThrows(DataIntegrityViolationException.class, () -> jdbcTemplate.update(
                "INSERT INTO study_activities (user_id, activity_date, activity_type) VALUES (?, CURRENT_DATE, 'DORMIR')",
                user.getId()));
    }

    @Test
    @DisplayName("Banco: o mesmo tipo de atividade só é registrado uma vez por dia (UNIQUE composto)")
    void shouldRejectDuplicateActivityOnSameDay() {
        User user = userRepository.save(new User("Aluno", "unico@teste.com", "hash"));
        studyActivityRepository.saveAndFlush(new StudyActivity(user, LocalDate.now(), ActivityType.QUIZ));

        assertThrows(DataIntegrityViolationException.class,
                () -> studyActivityRepository.saveAndFlush(new StudyActivity(user, LocalDate.now(), ActivityType.QUIZ)));
    }

    @Test
    @DisplayName("Banco: apagar usuário apaga dias de estudo e chat (ON DELETE CASCADE)")
    void shouldCascadeDeleteUserData() {
        User user = userRepository.save(new User("Aluno", "cascata@teste.com", "hash"));
        studyActivityRepository.save(new StudyActivity(user, LocalDate.now(), ActivityType.CHAT));
        chatMessageRepository.save(new ChatMessage(user, null, "user", "Olá"));

        jdbcTemplate.update("DELETE FROM users WHERE id = ?", user.getId());

        assertEquals(0, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM study_activities WHERE user_id = ?", Integer.class, user.getId()));
        assertEquals(0, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chat_messages WHERE user_id = ?", Integer.class, user.getId()));
    }

    @Test
    @DisplayName("Banco: apagar matéria mantém o compromisso da agenda, só tira a ligação (ON DELETE SET NULL)")
    void shouldSetNullOnScheduleWhenSubjectIsDeleted() {
        User user = userRepository.save(new User("Aluno", "agenda@teste.com", "hash"));
        Subject subject = subjectRepository.save(new Subject("História", "Geral", user));
        Schedule schedule = new Schedule("Revisar", "História", "historia", "📖", LocalDate.now(), 0, "10:00", user);
        schedule.setSubject(subject);
        schedule = scheduleRepository.save(schedule);

        jdbcTemplate.update("DELETE FROM subjects WHERE id = ?", subject.getId());

        assertNull(jdbcTemplate.queryForObject(
                "SELECT subject_id FROM schedules WHERE id = ?", Long.class, schedule.getId()));
    }
}
