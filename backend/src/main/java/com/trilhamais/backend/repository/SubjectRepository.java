package com.trilhamais.backend.repository;

import com.trilhamais.backend.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para operacoes de persistencia da entidade Subject.
 * Todas as consultas sao isoladas por userId para manter a integridade multitenancy.
 */
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    List<Subject> findByUserIdOrderByNameAsc(Long userId);

    Optional<Subject> findByIdAndUserId(Long id, Long userId);

    boolean existsByNameIgnoreCaseAndUserId(String name, Long userId);

    boolean existsByNameIgnoreCaseAndUserIdAndIdNot(String name, Long userId, Long id);
}
