package com.scrumapp.backend.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo para actualizar un Project, segun
 * docs/api/components/schemas/project.yaml#/UpdateProjectRequest.
 */
public record UpdateProjectRequest(
        @JsonProperty("name")
                @NotBlank(message = "es obligatorio")
                @Size(max = 255, message = "no puede superar los 255 caracteres")
                String name,
        @JsonProperty("description")
                @Size(max = 1000, message = "no puede superar los 1000 caracteres")
                String description) {
}
