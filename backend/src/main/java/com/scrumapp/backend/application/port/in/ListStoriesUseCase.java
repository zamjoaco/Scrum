package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.story.UserStory;
import java.util.List;
import java.util.UUID;

/**
 * Caso de uso: listar las user stories de un project, con filtros
 * opcionales por sprintId y statusId.
 */
public interface ListStoriesUseCase {

    List<UserStory> listStories(
            UUID projectId, UUID requesterUserId, UUID sprintId, UUID statusId);
}
