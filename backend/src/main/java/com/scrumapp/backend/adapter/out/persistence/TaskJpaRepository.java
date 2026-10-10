package com.scrumapp.backend.adapter.out.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data JPA para TaskJpaEntity. Uso interno del
 * subpaquete de persistencia; la aplicacion depende de TaskRepository
 * (puerto de salida).
 */
public interface TaskJpaRepository extends JpaRepository<TaskJpaEntity, UUID> {

    List<TaskJpaEntity> findByUserStoryId(UUID userStoryId);
}
