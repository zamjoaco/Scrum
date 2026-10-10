package com.scrumapp.backend.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data JPA para BoardJpaEntity. Uso interno del
 * subpaquete de persistencia; la aplicacion depende de BoardRepository
 * (puerto de salida).
 */
public interface BoardJpaRepository extends JpaRepository<BoardJpaEntity, UUID> {

    Optional<BoardJpaEntity> findByProjectId(UUID projectId);
}
