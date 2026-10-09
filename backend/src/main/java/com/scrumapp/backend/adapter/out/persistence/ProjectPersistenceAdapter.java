package com.scrumapp.backend.adapter.out.persistence;

import com.scrumapp.backend.application.port.out.ProjectRepository;
import com.scrumapp.backend.domain.project.Project;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Adaptador de persistencia que implementa el puerto ProjectRepository.
 * Traduce explicitamente entre la entidad de dominio Project y la entidad JPA
 * ProjectJpaEntity; esta ultima no sale nunca de este subpaquete.
 */
@Repository
public class ProjectPersistenceAdapter implements ProjectRepository {

    private final ProjectJpaRepository jpaRepository;

    public ProjectPersistenceAdapter(ProjectJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Project> findById(UUID id) {
        return jpaRepository.findById(id).map(ProjectPersistenceAdapter::toDomain);
    }

    @Override
    public Project save(Project project) {
        ProjectJpaEntity saved = jpaRepository.save(toJpaEntity(project));
        return toDomain(saved);
    }

    @Override
    public List<Project> listByWorkspaceId(UUID workspaceId) {
        return jpaRepository.findByWorkspaceId(workspaceId).stream()
                .map(ProjectPersistenceAdapter::toDomain)
                .toList();
    }

    private static ProjectJpaEntity toJpaEntity(Project project) {
        return new ProjectJpaEntity(
                project.getId(),
                project.getWorkspaceId(),
                project.getName(),
                project.getKey(),
                project.getDescription(),
                toLocalDateTime(project.getArchivedAt()),
                toLocalDateTime(project.getCreatedAt()));
    }

    private static Project toDomain(ProjectJpaEntity entity) {
        return new Project(
                entity.getId(),
                entity.getWorkspaceId(),
                entity.getName(),
                entity.getKey(),
                entity.getDescription(),
                toInstant(entity.getArchivedAt()),
                toInstant(entity.getCreatedAt()));
    }

    private static LocalDateTime toLocalDateTime(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    private static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.toInstant(ZoneOffset.UTC);
    }
}
