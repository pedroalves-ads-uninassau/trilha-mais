package com.trilhamais.backend.controller;

import com.trilhamais.backend.dto.RegisterRequest;
import com.trilhamais.backend.dto.UserResponse;
import com.trilhamais.backend.model.QuizStatus;
import com.trilhamais.backend.model.User;
import com.trilhamais.backend.repository.QuizRepository;
import com.trilhamais.backend.repository.SubjectRepository;
import com.trilhamais.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST responsavel pelas operacoes relacionadas a usuarios.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final SubjectRepository subjectRepository;
    private final QuizRepository quizRepository;

    public UserController(UserService userService, SubjectRepository subjectRepository, QuizRepository quizRepository) {
        this.userService = userService;
        this.subjectRepository = subjectRepository;
        this.quizRepository = quizRepository;
    }

    /**
     * Endpoint para cadastro de um novo usuario.
     * POST /api/users/register
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint protegido para obtencao do perfil do usuario autenticado com contadores de estudos.
     * GET /api/users/profile
     * Requer cabecalho Authorization: Bearer <token_jwt>
     */
    @GetMapping({"/profile", "/me"})
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        int subjectsCount = (int) subjectRepository.countByUserId(user.getId());
        int evaluationsCount = (int) quizRepository.countByUserIdAndStatus(user.getId(), QuizStatus.COMPLETED);

        return ResponseEntity.ok(new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getStreakDays(),
                evaluationsCount,
                subjectsCount,
                user.getAvatarEmoji()
        ));
    }
}
