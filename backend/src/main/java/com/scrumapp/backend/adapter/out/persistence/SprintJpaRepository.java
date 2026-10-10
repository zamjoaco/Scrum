package com.scrumapp.backend.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data JPA para SprintJpaEntity. Uso interno del
 * subpaquete de persistencia; la aplicacion depende de SprintRepository
 * (puerto de salida).
 */
public interface SprintJpaRepository extends JpaRepository<SprintJpaEntity, UUID> {

    List<SprintJpaEntity> findByProjectId(UUID projectId);

    Optional<SprintJpaEntity> findByProjectIdAndStatus(UUID projectId, String status);
}
