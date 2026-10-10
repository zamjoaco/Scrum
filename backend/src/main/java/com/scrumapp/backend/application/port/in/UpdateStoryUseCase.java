package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.story.UserStory;
import java.util.UUID;

/**
 * Caso de uso: actualizar una user story (merge patch). Cualquier parametro
 * que llegue en null significa "no modificar ese campo".
 */
public interface UpdateStoryUseCase {

    UserStory updateStory(
            UUID storyId,
            UUID requesterUserId,
            String title,
            String description,
            UserStory.Priority priority,
            Integer storyPoints,
            UUID assigneeId,
            UUID sprintId);
}
