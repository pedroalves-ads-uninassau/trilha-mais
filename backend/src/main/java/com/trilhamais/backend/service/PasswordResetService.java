package com.trilhamais.backend.service;

import com.trilhamais.backend.dto.ForgotPasswordRequest;
import com.trilhamais.backend.dto.ResetPasswordRequest;
import com.trilhamais.backend.model.PasswordResetToken;
import com.trilhamais.backend.model.User;
import com.trilhamais.backend.repository.PasswordResetTokenRepository;
import com.trilhamais.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

/**
 * Servico de recuperacao de senha por codigo de 6 digitos enviado ao e-mail do estudante.
 * - O codigo vale por 30 minutos e aceita no maximo 5 tentativas.
 * - No banco fica apenas o hash SHA-256 do codigo.
 * - Se o envio de e-mail nao estiver configurado (spring.mail.host), o codigo aparece no log do servidor
 *   para facilitar os testes em desenvolvimento.
 */
@Service
public class PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);
    private static final int CODE_VALIDITY_MINUTES = 30;
    private static final int MAX_ATTEMPTS = 5;
    private static final String INVALID_CODE_MESSAGE = "Código inválido ou expirado. Solicite um novo código.";

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final String mailFrom;
    private final SecureRandom random = new SecureRandom();

    public PasswordResetService(UserRepository userRepository,
                                PasswordResetTokenRepository tokenRepository,
                                PasswordEncoder passwordEncoder,
                                ObjectProvider<JavaMailSender> mailSenderProvider,
                                @Value("${app.mail.from:nao-responda@trilhamais.app}") String mailFrom) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSenderProvider = mailSenderProvider;
        this.mailFrom = mailFrom;
    }

    /**
     * Gera e envia o codigo de recuperacao. Por seguranca, nao revela se o e-mail esta cadastrado.
     */
    @Transactional
    public void requestReset(ForgotPasswordRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        Optional<User> userOpt = userRepository.findByEmail(normalizedEmail);
        if (userOpt.isEmpty()) {
            return;
        }
        User user = userOpt.get();

        // Invalida codigos anteriores ainda nao usados
        LocalDateTime now = LocalDateTime.now();
        tokenRepository.findByUserIdAndUsedAtIsNull(user.getId()).forEach(t -> t.setUsedAt(now));

        String code = String.format("%06d", random.nextInt(1_000_000));
        tokenRepository.save(new PasswordResetToken(user, hash(code), now.plusMinutes(CODE_VALIDITY_MINUTES)));

        sendCode(user, code);
    }

    /**
     * Confere o codigo e grava a nova senha (hash BCrypt).
     */
    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public void resetPassword(ResetPasswordRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_CODE_MESSAGE));

        PasswordResetToken token = tokenRepository.findFirstByUserIdAndUsedAtIsNullOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new IllegalArgumentException(INVALID_CODE_MESSAGE));

        if (token.getExpiresAt().isBefore(LocalDateTime.now()) || token.getAttempts() >= MAX_ATTEMPTS) {
            token.setUsedAt(LocalDateTime.now());
            throw new IllegalArgumentException(INVALID_CODE_MESSAGE);
        }

        if (!token.getTokenHash().equals(hash(request.getCode().trim()))) {
            // Conta a tentativa errada (fica salva mesmo com a excecao, por causa do noRollbackFor)
            token.setAttempts(token.getAttempts() + 1);
            throw new IllegalArgumentException(INVALID_CODE_MESSAGE);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        token.setUsedAt(LocalDateTime.now());
    }

    private void sendCode(User user, String code) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.warn("Envio de e-mail não configurado (spring.mail.host). Código de recuperação para {}: {}",
                    user.getEmail(), code);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(user.getEmail());
        message.setSubject("Trilha+ — Código para criar uma nova senha");
        message.setText("Olá, " + user.getName() + "!\n\n"
                + "Seu código para criar uma nova senha no Trilha+ é: " + code + "\n\n"
                + "Ele vale por " + CODE_VALIDITY_MINUTES + " minutos. "
                + "Se você não pediu a troca de senha, ignore este e-mail.");
        try {
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail de recuperação para {}: {}", user.getEmail(), e.getMessage());
        }
    }

    private static String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponível.", e);
        }
    }
}
