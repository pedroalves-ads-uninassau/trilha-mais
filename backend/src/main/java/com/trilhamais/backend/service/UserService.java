package com.trilhamais.backend.service;

import com.trilhamais.backend.dto.RegisterRequest;
import com.trilhamais.backend.dto.UserResponse;
import com.trilhamais.backend.exception.EmailAlreadyExistsException;
import com.trilhamais.backend.model.User;
import com.trilhamais.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Camada de servico com as regras de negocio do usuario.
 * Responsavel por validar unicidade de e-mail, gerar hash BCrypt da senha e persistir a entidade.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
}
