package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.story.UserStory;
import java.util.UUID;

/**
 * Caso de uso: crear una user story dentro de un project. La historia
 * nueva entra siempre al Product Backlog (sprintId null) y en la primera
 * columna del board del project.
 */
public interface CreateStoryUseCase {

    UserStory createStory(
            UUID projectId,
            UUID requesterUserId,
            String title,
            String description,
            UserStory.Priority priority,
            Integer storyPoints);
}
