package com.scrumapp.backend.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * Cuerpo para crear una Task, segun
 * docs/api/components/schemas/task.yaml#/CreateTaskRequest.
 */
public record CreateTaskRequest(
        @JsonProperty("title")
                @NotBlank(message = "es obligatorio")
                @Size(max = 255, message = "no puede superar los 255 caracteres")
                String title,
        @JsonProperty("assignee_id") UUID assigneeId) {
}
