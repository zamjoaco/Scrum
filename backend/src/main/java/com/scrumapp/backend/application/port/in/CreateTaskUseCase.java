package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.task.Task;
import java.util.UUID;

/**
 * Caso de uso: crear una Task (subtarea) dentro de una UserStory. La Task
 * nueva entra con el mismo statusId que tenga la UserStory padre en ese
 * momento.
 */
public interface CreateTaskUseCase {

    Task createTask(UUID userStoryId, UUID requesterUserId, String title, UUID assigneeId);
}
