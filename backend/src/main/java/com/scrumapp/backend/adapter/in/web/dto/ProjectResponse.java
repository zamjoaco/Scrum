package com.scrumapp.backend.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scrumapp.backend.domain.project.Project;
import java.time.Instant;
import java.util.UUID;

/**
 * Representacion de salida de un Project, con nombres de campo snake_case
 * segun docs/api/components/schemas/project.yaml#/Project.
 */
public record ProjectResponse(
        @JsonProperty("id") UUID id,
        @JsonProperty("workspace_id") UUID workspaceId,
        @JsonProperty("name") String name,
        @JsonProperty("key") String key,
        @JsonProperty("description") String description,
        @JsonProperty("archived_at") Instant archivedAt,
        @JsonProperty("created_at") Instant createdAt) {

    public static ProjectResponse from(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getWorkspaceId(),
                project.getName(),
                project.getKey(),
                project.getDescription(),
                project.getArchivedAt(),
                project.getCreatedAt());
    }
}
