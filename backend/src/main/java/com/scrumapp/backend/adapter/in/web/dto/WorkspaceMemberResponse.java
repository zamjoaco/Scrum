package com.scrumapp.backend.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scrumapp.backend.domain.user.Role;
import com.scrumapp.backend.domain.workspace.WorkspaceMember;
import java.time.Instant;
import java.util.UUID;

/**
 * Representacion de salida de una membresia de workspace, con nombres de
 * campo snake_case segun
 * docs/api/components/schemas/workspace.yaml#/WorkspaceMember.
 */
public record WorkspaceMemberResponse(
        @JsonProperty("id") UUID id,
        @JsonProperty("workspace_id") UUID workspaceId,
        @JsonProperty("user_id") UUID userId,
        @JsonProperty("role") Role role,
        @JsonProperty("joined_at") Instant joinedAt) {

    public static WorkspaceMemberResponse from(WorkspaceMember member) {
        return new WorkspaceMemberResponse(
                member.getId(),
                member.getWorkspaceId(),
                member.getUserId(),
                member.getRole(),
                member.getJoinedAt());
    }
}
