package com.scrumapp.backend.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scrumapp.backend.domain.workspace.Workspace;
import java.time.Instant;
import java.util.UUID;

/**
 * Representacion de salida de un workspace, con nombres de campo snake_case
 * segun docs/api/components/schemas/workspace.yaml#/Workspace.
 */
public record WorkspaceResponse(
        @JsonProperty("id") UUID id,
        @JsonProperty("name") String name,
        @JsonProperty("slug") String slug,
        @JsonProperty("owner_id") UUID ownerId,
        @JsonProperty("created_at") Instant createdAt) {

    public static WorkspaceResponse from(Workspace workspace) {
        return new WorkspaceResponse(
                workspace.getId(),
                workspace.getName(),
                workspace.getSlug(),
                workspace.getOwnerId(),
                workspace.getCreatedAt());
    }
}
