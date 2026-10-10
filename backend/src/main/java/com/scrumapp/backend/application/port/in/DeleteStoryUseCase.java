package com.scrumapp.backend.application.port.in;

import java.util.UUID;

/** Caso de uso: borrar (hard delete) una user story. */
public interface DeleteStoryUseCase {

    void deleteStory(UUID storyId, UUID requesterUserId);
}
