package com.scrumapp.backend.adapter.out.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio Spring Data JPA para WorkspaceJpaEntity. Uso interno del
 * subpaquete de persistencia; la aplicacion depende de WorkspaceRepository
 * (puerto de salida).
 */
public interface WorkspaceJpaRepository extends JpaRepository<WorkspaceJpaEntity, UUID> {

    @Query(
            "select w from WorkspaceJpaEntity w "
                    + "join WorkspaceMemberJpaEntity m on m.workspaceId = w.id "
                    + "where m.userId = :userId")
    List<WorkspaceJpaEntity> findAllForUser(@Param("userId") UUID userId);
}
