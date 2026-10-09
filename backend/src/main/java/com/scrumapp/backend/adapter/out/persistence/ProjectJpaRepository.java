package com.scrumapp.backend.adapter.out.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data JPA para ProjectJpaEntity. Uso interno del
 * subpaquete de persistencia; la aplicacion depende de ProjectRepository
 * (puerto de salida).
 */
public interface ProjectJpaRepository extends JpaRepository<ProjectJpaEntity, UUID> {

    List<ProjectJpaEntity> findByWorkspaceId(UUID workspaceId);
}
