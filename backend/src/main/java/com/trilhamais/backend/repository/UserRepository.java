package com.trilhamais.backend.repository;

import com.trilhamais.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interface Repository para a entidade User.
 * Herda metodos padrao de persistencia (save, findById, findAll, delete, etc.) do JpaRepository.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Busca um usuario pelo endereco de e-mail
    Optional<User> findByEmail(String email);

    // Verifica se ja existe um usuario cadastrado com determinado e-mail
    boolean existsByEmail(String email);
}
