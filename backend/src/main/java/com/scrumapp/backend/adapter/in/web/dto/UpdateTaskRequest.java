package com.scrumapp.backend.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * Cuerpo para actualizar una Task (merge patch, todos los campos
 * opcionales), segun docs/api/components/schemas/task.yaml#/UpdateTaskRequest.
 */
public record UpdateTaskRequest(
        @JsonProperty("title")
                @Size(max = 255, message = "no puede superar los 255 caracteres")
                String title,
        @JsonProperty("assignee_id") UUID assigneeId,
        @JsonProperty("status_id") UUID statusId) {
}
