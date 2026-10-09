package com.scrumapp.backend.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo de actualizacion de workspace, segun
 * docs/api/components/schemas/workspace.yaml#/UpdateWorkspaceRequest.
 */
public record UpdateWorkspaceRequest(
        @JsonProperty("name")
                @NotBlank(message = "es obligatorio")
                @Size(max = 255, message = "no puede superar los 255 caracteres")
                String name) {
}
