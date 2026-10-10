package com.scrumapp.backend.adapter.out.persistence;

import com.scrumapp.backend.application.port.out.UserStoryRepository;
import com.scrumapp.backend.domain.story.UserStory;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Adaptador de persistencia que implementa el puerto UserStoryRepository.
 * Traduce explicitamente entre la entidad de dominio UserStory y la entidad
 * JPA UserStoryJpaEntity; esta ultima no sale nunca de este subpaquete.
 */
@Repository
public class UserStoryPersistenceAdapter implements UserStoryRepository {

    private final UserStoryJpaRepository jpaRepository;

    public UserStoryPersistenceAdapter(UserStoryJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<UserStory> findById(UUID id) {
        return jpaRepository.findById(id).map(UserStoryPersistenceAdapter::toDomain);
    }

    @Override
    public UserStory save(UserStory userStory) {
        UserStoryJpaEntity saved = jpaRepository.save(toJpaEntity(userStory));
        return toDomain(saved);
    }

    @Override
    public void delete(UserStory userStory) {
        jpaRepository.deleteById(userStory.getId());
    }

    @Override
    public List<UserStory> listByProjectId(UUID projectId, UUID sprintId, UUID statusId) {
        return jpaRepository.findByProjectId(projectId).stream()
                .filter(entity -> sprintId == null || sprintId.equals(entity.getSprintId()))
                .filter(entity -> statusId == null || statusId.equals(entity.getStatusId()))
                .map(UserStoryPersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    public long countByStatusId(UUID columnId) {
        return jpaRepository.countByStatusId(columnId);
    }

    @Override
    public void reassignSprintToNull(UUID sprintId) {
        jpaRepository.reassignSprintToNull(sprintId);
    }

    private static UserStoryJpaEntity toJpaEntity(UserStory userStory) {
        return new UserStoryJpaEntity(
                userStory.getId(),
                userStory.getProjectId(),
                userStory.getSprintId(),
                userStory.getStatusId(),
                userStory.getAssigneeId(),
                userStory.getCreatedBy(),
                userStory.getTitle(),
                userStory.getDescription(),
                userStory.getStoryPoints(),
                userStory.getPriority(),
                toLocalDateTime(userStory.getCreatedAt()));
    }

    private static UserStory toDomain(UserStoryJpaEntity entity) {
        return new UserStory(
                entity.getId(),
                entity.getProjectId(),
                entity.getSprintId(),
                entity.getStatusId(),
                entity.getAssigneeId(),
                entity.getCreatedBy(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStoryPoints(),
                entity.getPriority(),
                toInstant(entity.getCreatedAt()));
    }

    private static LocalDateTime toLocalDateTime(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    private static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.toInstant(ZoneOffset.UTC);
    }
}
