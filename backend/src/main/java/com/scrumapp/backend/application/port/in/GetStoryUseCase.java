package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.story.UserStory;
import java.util.UUID;

/** Caso de uso: obtener una user story por id. */
public interface GetStoryUseCase {

    UserStory getStory(UUID storyId, UUID requesterUserId);
}
