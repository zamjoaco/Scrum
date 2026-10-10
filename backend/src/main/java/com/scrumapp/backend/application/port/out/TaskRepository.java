package com.scrumapp.backend.application.port.out;

import com.scrumapp.backend.domain.task.Task;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para persistencia de tasks. Trabaja exclusivamente con
 * la entidad de dominio Task, nunca con una entidad JPA.
 */
public interface TaskRepository {

    Optional<Task> findById(UUID id);

    Task save(Task task);

    void delete(Task task);

    List<Task> listByUserStoryId(UUID userStoryId);
}
