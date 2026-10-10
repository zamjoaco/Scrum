package com.scrumapp.backend.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Cuerpo para mover una UserStory a otra BoardColumn, segun
 * docs/api/components/schemas/story.yaml#/MoveStoryRequest.
 */
public record MoveStoryRequest(
        @JsonProperty("target_column_id") @NotNull(message = "es obligatorio") UUID targetColumnId) {
}
