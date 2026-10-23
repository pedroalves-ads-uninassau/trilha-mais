package com.trilhamais.backend.service;

import com.trilhamais.backend.dto.ChangePasswordRequest;
import com.trilhamais.backend.dto.RegisterRequest;
import com.trilhamais.backend.dto.UpdateProfileRequest;
import com.trilhamais.backend.dto.UserResponse;
import com.trilhamais.backend.dto.UserSettingsDto;
import com.trilhamais.backend.exception.EmailAlreadyExistsException;
import com.trilhamais.backend.exception.ResourceNotFoundException;
import com.trilhamais.backend.model.QuizStatus;
import com.trilhamais.backend.model.User;
import com.trilhamais.backend.model.UserSettings;
import com.trilhamais.backend.repository.QuizRepository;
import com.trilhamais.backend.repository.SubjectRepository;
import com.trilhamais.backend.repository.UserRepository;
import com.trilhamais.backend.repository.UserSettingsRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Camada de servico com as regras de negocio do usuario.
 * Responsavel por validar unicidade de e-mail, gerar hash BCrypt da senha e persistir a entidade,
 * alem de montar o perfil, editar dados e guardar as configuracoes do estudante.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SubjectRepository subjectRepository;
    private final QuizRepository quizRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final StudyActivityService studyActivityService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       SubjectRepository subjectRepository, QuizRepository quizRepository,
                       UserSettingsRepository userSettingsRepository, StudyActivityService studyActivityService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.subjectRepository = subjectRepository;
        this.quizRepository = quizRepository;
        this.userSettingsRepository = userSettingsRepository;
        this.studyActivityService = studyActivityService;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        // Normaliza o e-mail removendo espacos e convertendo para minusculo
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        // 1. Verifica se o e-mail ja esta em uso
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException("E-mail já cadastrado.");
        }

        // 2. Criptografa a senha com BCrypt (nunca salva em texto puro)
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // 3. Cria e salva o novo usuario
        User user = new User(request.getName().trim(), normalizedEmail, hashedPassword);
        User savedUser = userRepository.save(user);

        // 4. Retorna apenas os dados publicos necessarios (sem senha)
        return new UserResponse(savedUser.getId(), savedUser.getName(), savedUser.getEmail());
    }

    /**
     * Monta o perfil do estudante com contadores e a sequencia de dias calculada na hora.
     */
    @Transactional(readOnly = true)
    public UserResponse getProfile(User user) {
        int subjectsCount = (int) subjectRepository.countByUserId(user.getId());
        int evaluationsCount = (int) quizRepository.countByUserIdAndStatus(user.getId(), QuizStatus.COMPLETED);

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                studyActivityService.getCurrentStreak(user),
                evaluationsCount,
                subjectsCount,
                user.getAvatarEmoji()
        );
    }

    /**
     * Edita nome e avatar do estudante.
     */
    @Transactional
    public UserResponse updateProfile(User user, UpdateProfileRequest request) {
        User entity = findUser(user);
        entity.setName(request.getName().trim());
        if (request.getAvatarEmoji() != null && !request.getAvatarEmoji().isBlank()) {
            entity.setAvatarEmoji(request.getAvatarEmoji().trim());
        }
        User saved = userRepository.save(entity);
        return getProfile(saved);
    }

    /**
     * Troca a senha do estudante logado, conferindo a senha atual.
     */
    @Transactional
    public void changePassword(User user, ChangePasswordRequest request) {
        User entity = findUser(user);
        if (!passwordEncoder.matches(request.getCurrentPassword(), entity.getPassword())) {
            // 400 (e nao 401) para o app nao confundir com token expirado
            throw new IllegalArgumentException("Senha atual incorreta.");
        }
        entity.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(entity);
    }

    /**
     * Retorna as configuracoes do estudante (padrao: notificacoes ligadas e tema claro).
     */
    @Transactional(readOnly = true)
    public UserSettingsDto getSettings(User user) {
        return userSettingsRepository.findByUserId(user.getId())
                .map(this::toSettingsDto)
                .orElseGet(() -> toSettingsDto(new UserSettings(user)));
    }

    /**
     * Atualiza as configuracoes. Campos nao enviados continuam como estavam.
     */
    @Transactional
    public UserSettingsDto updateSettings(User user, UserSettingsDto request) {
        UserSettings settings = userSettingsRepository.findByUserId(user.getId())
                .orElseGet(() -> new UserSettings(findUser(user)));

        if (request.getNotificationsEnabled() != null) {
            settings.setNotificationsEnabled(request.getNotificationsEnabled());
        }
        if (request.getTheme() != null) {
            String theme = request.getTheme().trim().toLowerCase();
            if (!theme.equals("light") && !theme.equals("dark")) {
                throw new IllegalArgumentException("Tema inválido. Use 'light' ou 'dark'.");
            }
            settings.setTheme(theme);
        }
        return toSettingsDto(userSettingsRepository.save(settings));
    }

    private User findUser(User user) {
        return userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
    }

    private UserSettingsDto toSettingsDto(UserSettings settings) {
        return new UserSettingsDto(settings.isNotificationsEnabled(), settings.getTheme());
    }
}
