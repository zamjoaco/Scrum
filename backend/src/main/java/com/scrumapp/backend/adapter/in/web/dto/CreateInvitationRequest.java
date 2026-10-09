package com.scrumapp.backend.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scrumapp.backend.domain.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Cuerpo de creacion de invitacion, segun
 * docs/api/components/schemas/workspace.yaml#/CreateInvitationRequest.
 */
public record CreateInvitationRequest(
        @JsonProperty("email")
                @NotBlank(message = "es obligatorio")
                @Email(message = "debe ser un email valido")
                String email,
        @JsonProperty("role") @NotNull(message = "es obligatorio") Role role) {
}
