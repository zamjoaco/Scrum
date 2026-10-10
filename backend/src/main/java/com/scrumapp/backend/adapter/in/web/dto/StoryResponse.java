package com.scrumapp.backend.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scrumapp.backend.domain.story.UserStory;
import java.time.Instant;
import java.util.UUID;

/**
 * Representacion de salida de una UserStory, con nombres de campo
 * snake_case segun docs/api/components/schemas/story.yaml#/UserStory.
 */
public record StoryResponse(
        @JsonProperty("id") UUID id,
        @JsonProperty("project_id") UUID projectId,
        @JsonProperty("sprint_id") UUID sprintId,
        @JsonProperty("status_id") UUID statusId,
        @JsonProperty("assignee_id") UUID assigneeId,
        @JsonProperty("created_by") UUID createdBy,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("story_points") Integer storyPoints,
        @JsonProperty("priority") UserStory.Priority priority,
        @JsonProperty("created_at") Instant createdAt) {

    public static StoryResponse from(UserStory userStory) {
        return new StoryResponse(
                userStory.getId(),
                userStory.getProjectId(),
                userStory.getSprintId(),
                userStory.getStatusId(),
                userStory.getAssigneeId(),
                userStory.getCreatedBy(),
                userStory.getTitle(),
                userStory.getDescription(),
                userStory.getStoryPoints(),
                userStory.getPriority(),
                userStory.getCreatedAt());
    }
}
