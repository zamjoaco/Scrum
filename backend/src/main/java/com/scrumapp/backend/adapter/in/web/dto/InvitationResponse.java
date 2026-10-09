package com.scrumapp.backend.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scrumapp.backend.domain.user.Role;
import com.scrumapp.backend.domain.workspace.Invitation;
import java.time.Instant;
import java.util.UUID;

/**
 * Representacion de salida de una invitacion, con nombres de campo
 * snake_case segun docs/api/components/schemas/workspace.yaml#/Invitation.
 */
public record InvitationResponse(
        @JsonProperty("id") UUID id,
        @JsonProperty("workspace_id") UUID workspaceId,
        @JsonProperty("email") String email,
        @JsonProperty("role") Role role,
        @JsonProperty("status") Invitation.Status status,
        @JsonProperty("expires_at") Instant expiresAt) {

    public static InvitationResponse from(Invitation invitation) {
        return new InvitationResponse(
                invitation.getId(),
                invitation.getWorkspaceId(),
                invitation.getEmail(),
                invitation.getRole(),
                invitation.getStatus(),
                invitation.getExpiresAt());
    }
}
