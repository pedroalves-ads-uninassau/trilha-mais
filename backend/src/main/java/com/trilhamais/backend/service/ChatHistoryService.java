package com.trilhamais.backend.service;

import com.trilhamais.backend.dto.AiChatMessageDto;
import com.trilhamais.backend.dto.ChatMessageResponse;
import com.trilhamais.backend.exception.ResourceNotFoundException;
import com.trilhamais.backend.model.ActivityType;
import com.trilhamais.backend.model.ChatMessage;
import com.trilhamais.backend.model.Subject;
import com.trilhamais.backend.model.User;
import com.trilhamais.backend.repository.ChatMessageRepository;
import com.trilhamais.backend.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servico que guarda e devolve o historico do chat com o tutor de IA.
 * A cada envio salvamos a ultima pergunta do estudante e a resposta da IA.
 */
@Service
public class ChatHistoryService {

    private final ChatMessageRepository chatMessageRepository;
    private final SubjectRepository subjectRepository;
    private final StudyActivityService studyActivityService;

    public ChatHistoryService(ChatMessageRepository chatMessageRepository, SubjectRepository subjectRepository,
                              StudyActivityService studyActivityService) {
        this.chatMessageRepository = chatMessageRepository;
        this.subjectRepository = subjectRepository;
        this.studyActivityService = studyActivityService;
    }

    @Transactional
    public void saveExchange(User user, Long subjectId, List<AiChatMessageDto> messages, String reply) {
        Subject subject = findSubject(user, subjectId);

        // Ultima mensagem enviada pelo estudante (as anteriores ja foram salvas nos envios passados)
        for (int i = messages.size() - 1; i >= 0; i--) {
            AiChatMessageDto m = messages.get(i);
            if ("user".equalsIgnoreCase(m.getRole()) && m.getContent() != null && !m.getContent().isBlank()) {
                chatMessageRepository.save(new ChatMessage(user, subject, "user", m.getContent()));
                break;
            }
        }
        chatMessageRepository.save(new ChatMessage(user, subject, "assistant", reply));
        studyActivityService.registerActivity(user, ActivityType.CHAT);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getHistory(User user, Long subjectId) {
        List<ChatMessage> messages = subjectId != null
                ? chatMessageRepository.findByUserIdAndSubjectIdOrderByCreatedAtAscIdAsc(user.getId(), subjectId)
                : chatMessageRepository.findByUserIdOrderByCreatedAtAscIdAsc(user.getId());
        return messages.stream()
                .map(m -> new ChatMessageResponse(
                        m.getId(),
                        m.getSubject() != null ? m.getSubject().getId() : null,
                        m.getRole(),
                        m.getContent(),
                        m.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void clearHistory(User user, Long subjectId) {
        if (subjectId != null) {
            chatMessageRepository.deleteByUserIdAndSubjectId(user.getId(), subjectId);
        } else {
            chatMessageRepository.deleteByUserId(user.getId());
        }
    }

    private Subject findSubject(User user, Long subjectId) {
        if (subjectId == null) {
            return null;
        }
        return subjectRepository.findByIdAndUserId(subjectId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Matéria não encontrada com o id: " + subjectId));
    }
}
