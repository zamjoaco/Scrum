package com.scrumapp.backend.adapter.out.persistence;

import com.scrumapp.backend.application.port.out.WorkspaceRepository;
import com.scrumapp.backend.domain.workspace.Workspace;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Adaptador de persistencia que implementa el puerto WorkspaceRepository.
 * Traduce explicitamente entre la entidad de dominio Workspace y la entidad
 * JPA WorkspaceJpaEntity; esta ultima no sale nunca de este subpaquete.
 */
@Repository
public class WorkspacePersistenceAdapter implements WorkspaceRepository {

    private final WorkspaceJpaRepository jpaRepository;

    public WorkspacePersistenceAdapter(WorkspaceJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Workspace> findById(UUID id) {
        return jpaRepository.findById(id).map(WorkspacePersistenceAdapter::toDomain);
    }

    @Override
    public Workspace save(Workspace workspace) {
        WorkspaceJpaEntity saved = jpaRepository.save(toJpaEntity(workspace));
        return toDomain(saved);
    }

    @Override
    public void delete(Workspace workspace) {
        jpaRepository.deleteById(workspace.getId());
    }

    @Override
    public List<Workspace> listForUser(UUID userId) {
        return jpaRepository.findAllForUser(userId).stream()
                .map(WorkspacePersistenceAdapter::toDomain)
                .toList();
    }

    private static WorkspaceJpaEntity toJpaEntity(Workspace workspace) {
        return new WorkspaceJpaEntity(
                workspace.getId(),
                workspace.getName(),
                workspace.getSlug(),
                workspace.getOwnerId(),
                toLocalDateTime(workspace.getCreatedAt()));
    }

    private static Workspace toDomain(WorkspaceJpaEntity entity) {
        return new Workspace(
                entity.getId(),
                entity.getName(),
                entity.getSlug(),
                entity.getOwnerId(),
                toInstant(entity.getCreatedAt()));
    }

    private static LocalDateTime toLocalDateTime(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    private static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.toInstant(ZoneOffset.UTC);
    }
}
