package com.trilhamais.backend.repository;

import com.trilhamais.backend.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    // Codigo mais recente ainda nao utilizado do usuario
    Optional<PasswordResetToken> findFirstByUserIdAndUsedAtIsNullOrderByCreatedAtDesc(Long userId);

    List<PasswordResetToken> findByUserIdAndUsedAtIsNull(Long userId);
}
