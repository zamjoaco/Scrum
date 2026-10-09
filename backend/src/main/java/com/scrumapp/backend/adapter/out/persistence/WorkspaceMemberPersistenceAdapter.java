package com.scrumapp.backend.adapter.out.persistence;

import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.workspace.WorkspaceMember;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Adaptador de persistencia que implementa el puerto WorkspaceMemberRepository.
 * Traduce explicitamente entre la entidad de dominio WorkspaceMember y la
 * entidad JPA WorkspaceMemberJpaEntity; esta ultima no sale nunca de este
 * subpaquete.
 */
@Repository
public class WorkspaceMemberPersistenceAdapter implements WorkspaceMemberRepository {

    private final WorkspaceMemberJpaRepository jpaRepository;

    public WorkspaceMemberPersistenceAdapter(WorkspaceMemberJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<WorkspaceMember> findByWorkspaceIdAndUserId(UUID workspaceId, UUID userId) {
        return jpaRepository
                .findByWorkspaceIdAndUserId(workspaceId, userId)
                .map(WorkspaceMemberPersistenceAdapter::toDomain);
    }

    @Override
    public boolean isMember(UUID workspaceId, UUID userId) {
        return jpaRepository.existsByWorkspaceIdAndUserId(workspaceId, userId);
    }

    @Override
    public WorkspaceMember save(WorkspaceMember member) {
        WorkspaceMemberJpaEntity saved = jpaRepository.save(toJpaEntity(member));
        return toDomain(saved);
    }

    @Override
    public void delete(WorkspaceMember member) {
        jpaRepository.deleteById(member.getId());
    }

    @Override
    public List<WorkspaceMember> listByWorkspaceId(UUID workspaceId) {
        return jpaRepository.findByWorkspaceId(workspaceId).stream()
                .map(WorkspaceMemberPersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    public List<UUID> listWorkspaceIdsForUser(UUID userId) {
        return jpaRepository.findWorkspaceIdsByUserId(userId);
    }

    private static WorkspaceMemberJpaEntity toJpaEntity(WorkspaceMember member) {
        return new WorkspaceMemberJpaEntity(
                member.getId(),
                member.getWorkspaceId(),
                member.getUserId(),
                member.getRole(),
                toLocalDateTime(member.getJoinedAt()));
    }

    private static WorkspaceMember toDomain(WorkspaceMemberJpaEntity entity) {
        return new WorkspaceMember(
                entity.getId(),
                entity.getWorkspaceId(),
                entity.getUserId(),
                entity.getRole(),
                toInstant(entity.getJoinedAt()));
    }

    private static LocalDateTime toLocalDateTime(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    private static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.toInstant(ZoneOffset.UTC);
    }
}
