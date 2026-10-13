package com.trilhamais.backend.service;

import com.trilhamais.backend.dto.DashboardResponse;
import com.trilhamais.backend.dto.SubjectResponse;
import com.trilhamais.backend.model.QuizStatus;
import com.trilhamais.backend.model.Topic;
import com.trilhamais.backend.model.User;
import com.trilhamais.backend.repository.QuizRepository;
import com.trilhamais.backend.repository.SubjectRepository;
import com.trilhamais.backend.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DashboardService {

    private final SubjectService subjectService;
    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;
    private final QuizRepository quizRepository;

    public DashboardService(SubjectService subjectService, SubjectRepository subjectRepository,
                            TopicRepository topicRepository, QuizRepository quizRepository) {
        this.subjectService = subjectService;
        this.subjectRepository = subjectRepository;
        this.topicRepository = topicRepository;
        this.quizRepository = quizRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(User user) {
        List<SubjectResponse> subjects = subjectService.getAllSubjects(user);

        int totalSubjects = subjects.size();
        int totalTopics = subjects.stream().mapToInt(SubjectResponse::getTopicsCount).sum();
        int totalCompletedTopics = subjects.stream().mapToInt(SubjectResponse::getCompletedCount).sum();
        int totalEvaluations = (int) quizRepository.countByUserIdAndStatus(user.getId(), QuizStatus.COMPLETED);

        int overallProgress = totalTopics > 0 ? Math.round(((float) totalCompletedTopics / totalTopics) * 100) : 0;

        String motivationMessage = overallProgress >= 70
                ? "Você está indo muito bem! Continue assim!"
                : overallProgress >= 30
                ? "Você está evoluindo a cada dia!"
                : "Vamos começar seus estudos de hoje?";

        // Busca o primeiro tópico em andamento para continuar de onde parou
        String contSubject = null;
        String contTopic = null;
        String contColor = "portugues";

        for (SubjectResponse s : subjects) {
            List<Topic> topics = topicRepository.findBySubjectId(s.getId());
            for (Topic t : topics) {
                if ("andamento".equalsIgnoreCase(t.getStatus())) {
                    contSubject = s.getName();
                    contTopic = t.getName();
                    contColor = s.getColorKey();
                    break;
                }
            }
            if (contTopic != null) break;
        }

        // Se não houver nenhum em andamento, pega o primeiro pendente
        if (contTopic == null && !subjects.isEmpty()) {
            SubjectResponse first = subjects.get(0);
            List<Topic> topics = topicRepository.findBySubjectId(first.getId());
            if (!topics.isEmpty()) {
                contSubject = first.getName();
                contTopic = topics.get(0).getName();
                contColor = first.getColorKey();
            }
        }

        return new DashboardResponse(
                user.getName(),
                user.getStreakDays(),
                overallProgress,
                motivationMessage,
                totalSubjects,
                totalTopics,
                totalEvaluations,
                contSubject != null ? contSubject : "Selecione uma matéria",
                contTopic != null ? contTopic : "Comece uma matéria",
                contColor,
                subjects
        );
    }
}
