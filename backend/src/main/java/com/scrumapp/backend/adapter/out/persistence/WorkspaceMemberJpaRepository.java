package com.scrumapp.backend.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio Spring Data JPA para WorkspaceMemberJpaEntity. Uso interno del
 * subpaquete de persistencia; la aplicacion depende de
 * WorkspaceMemberRepository (puerto de salida).
 */
public interface WorkspaceMemberJpaRepository extends JpaRepository<WorkspaceMemberJpaEntity, UUID> {

    Optional<WorkspaceMemberJpaEntity> findByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);

    boolean existsByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);

    List<WorkspaceMemberJpaEntity> findByWorkspaceId(UUID workspaceId);

    @Query("select m.workspaceId from WorkspaceMemberJpaEntity m where m.userId = :userId")
    List<UUID> findWorkspaceIdsByUserId(@Param("userId") UUID userId);
}
