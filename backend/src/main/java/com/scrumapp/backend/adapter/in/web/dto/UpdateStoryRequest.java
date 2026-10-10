package com.scrumapp.backend.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scrumapp.backend.domain.story.UserStory.Priority;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * Cuerpo para actualizar una UserStory (merge patch, todos los campos
 * opcionales), segun docs/api/components/schemas/story.yaml#/UpdateStoryRequest.
 */
public record UpdateStoryRequest(
        @JsonProperty("title")
                @Size(max = 255, message = "no puede superar los 255 caracteres")
                String title,
        @JsonProperty("description")
                @Size(max = 2000, message = "no puede superar los 2000 caracteres")
                String description,
        @JsonProperty("priority") Priority priority,
        @JsonProperty("story_points") Integer storyPoints,
        @JsonProperty("assignee_id") UUID assigneeId,
        @JsonProperty("sprint_id") UUID sprintId) {
}
