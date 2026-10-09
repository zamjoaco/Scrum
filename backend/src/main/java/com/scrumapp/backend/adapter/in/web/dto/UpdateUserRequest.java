package com.scrumapp.backend.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo de actualizacion del perfil propio, segun
 * docs/api/components/schemas/user.yaml#/UpdateUserRequest.
 */
public record UpdateUserRequest(
        @JsonProperty("first_names")
                @NotBlank(message = "es obligatorio")
                @Size(max = 255, message = "no puede superar los 255 caracteres")
                String firstNames,
        @JsonProperty("last_names")
                @NotBlank(message = "es obligatorio")
                @Size(max = 255, message = "no puede superar los 255 caracteres")
                String lastNames) {
}
