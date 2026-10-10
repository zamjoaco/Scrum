package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.task.Task;
import java.util.List;
import java.util.UUID;

/** Caso de uso: listar las Task de una UserStory. */
public interface ListTasksUseCase {

    List<Task> listTasks(UUID userStoryId, UUID requesterUserId);
}
