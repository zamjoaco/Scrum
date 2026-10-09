package com.scrumapp.backend.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scrumapp.backend.domain.user.Role;
import jakarta.validation.constraints.NotNull;

/**
 * Cuerpo de actualizacion de rol de miembro, segun
 * docs/api/components/schemas/workspace.yaml#/UpdateMemberRoleRequest.
 */
public record UpdateMemberRoleRequest(
        @JsonProperty("role") @NotNull(message = "es obligatorio") Role role) {
}
