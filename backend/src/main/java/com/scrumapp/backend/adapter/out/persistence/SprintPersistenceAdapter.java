package com.scrumapp.backend.adapter.out.persistence;

import com.scrumapp.backend.application.port.out.SprintRepository;
import com.scrumapp.backend.domain.sprint.Sprint;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Adaptador de persistencia que implementa el puerto SprintRepository.
 * Traduce explicitamente entre la entidad de dominio Sprint y la entidad JPA
 * SprintJpaEntity; esta ultima no sale nunca de este subpaquete.
 */
@Repository
public class SprintPersistenceAdapter implements SprintRepository {

    private final SprintJpaRepository jpaRepository;

    public SprintPersistenceAdapter(SprintJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Sprint> findById(UUID id) {
        return jpaRepository.findById(id).map(SprintPersistenceAdapter::toDomain);
    }

    @Override
    public Sprint save(Sprint sprint) {
        SprintJpaEntity saved = jpaRepository.save(toJpaEntity(sprint));
        return toDomain(saved);
    }

    @Override
    public List<Sprint> listByProjectId(UUID projectId) {
        return jpaRepository.findByProjectId(projectId).stream()
                .map(SprintPersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    public Optional<Sprint> findActiveByProjectId(UUID projectId) {
        return jpaRepository
                .findByProjectIdAndStatus(projectId, Sprint.Status.ACTIVE.name())
                .map(SprintPersistenceAdapter::toDomain);
    }

    private static SprintJpaEntity toJpaEntity(Sprint sprint) {
        return new SprintJpaEntity(
                sprint.getId(),
                sprint.getProjectId(),
                sprint.getName(),
                sprint.getGoal(),
                sprint.getStartDate(),
                sprint.getEndDate(),
                sprint.getStatus().name());
    }

    private static Sprint toDomain(SprintJpaEntity entity) {
        return new Sprint(
                entity.getId(),
                entity.getProjectId(),
                entity.getName(),
                entity.getGoal(),
                entity.getStartDate(),
                entity.getEndDate(),
                Sprint.Status.valueOf(entity.getStatus()));
    }
}
