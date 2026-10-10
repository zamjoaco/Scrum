package com.scrumapp.backend.domain.story;

import com.scrumapp.backend.domain.user.DomainException;
import java.util.UUID;

public class StoryNotFoundException extends DomainException {

    public StoryNotFoundException(UUID storyId) {
        super("No existe una user story con id " + storyId);
    }
}
