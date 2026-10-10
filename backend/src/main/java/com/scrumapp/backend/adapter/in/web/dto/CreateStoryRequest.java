package com.scrumapp.backend.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scrumapp.backend.domain.story.UserStory.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo para crear una UserStory, segun
 * docs/api/components/schemas/story.yaml#/CreateStoryRequest.
 */
public record CreateStoryRequest(
        @JsonProperty("title")
                @NotBlank(message = "es obligatorio")
                @Size(max = 255, message = "no puede superar los 255 caracteres")
                String title,
        @JsonProperty("description")
                @Size(max = 2000, message = "no puede superar los 2000 caracteres")
                String description,
        @JsonProperty("priority") @NotNull(message = "es obligatorio") Priority priority,
        @JsonProperty("story_points") Integer storyPoints) {
}
