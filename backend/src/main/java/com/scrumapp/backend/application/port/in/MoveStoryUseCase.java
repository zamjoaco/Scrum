package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.story.UserStory;
import java.util.UUID;

/**
 * Caso de uso: mover una user story a otra BoardColumn, respetando el
 * wipLimit de la columna destino si tiene uno configurado.
 */
public interface MoveStoryUseCase {

    UserStory moveStory(UUID storyId, UUID requesterUserId, UUID targetColumnId);
}
