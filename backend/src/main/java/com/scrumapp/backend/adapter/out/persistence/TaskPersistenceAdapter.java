package com.scrumapp.backend.adapter.out.persistence;

import com.scrumapp.backend.application.port.out.TaskRepository;
import com.scrumapp.backend.domain.task.Task;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Adaptador de persistencia que implementa el puerto TaskRepository.
 * Traduce explicitamente entre la entidad de dominio Task y la entidad JPA
 * TaskJpaEntity; esta ultima no sale nunca de este subpaquete.
 */
@Repository
public class TaskPersistenceAdapter implements TaskRepository {

    private final TaskJpaRepository jpaRepository;

    public TaskPersistenceAdapter(TaskJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return jpaRepository.findById(id).map(TaskPersistenceAdapter::toDomain);
    }

    @Override
    public Task save(Task task) {
        TaskJpaEntity saved = jpaRepository.save(toJpaEntity(task));
        return toDomain(saved);
    }

    @Override
    public void delete(Task task) {
        jpaRepository.deleteById(task.getId());
    }

    @Override
    public List<Task> listByUserStoryId(UUID userStoryId) {
        return jpaRepository.findByUserStoryId(userStoryId).stream()
                .map(TaskPersistenceAdapter::toDomain)
                .toList();
    }

    private static TaskJpaEntity toJpaEntity(Task task) {
        return new TaskJpaEntity(
                task.getId(),
                task.getUserStoryId(),
                task.getStatusId(),
                task.getAssigneeId(),
                task.getTitle(),
                toLocalDateTime(task.getCreatedAt()));
    }

    private static Task toDomain(TaskJpaEntity entity) {
        return new Task(
                entity.getId(),
                entity.getUserStoryId(),
                entity.getStatusId(),
                entity.getAssigneeId(),
                entity.getTitle(),
                toInstant(entity.getCreatedAt()));
    }

    private static LocalDateTime toLocalDateTime(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    private static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.toInstant(ZoneOffset.UTC);
    }
}
