package com.scrumapp.backend.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scrumapp.backend.domain.task.Task;
import java.time.Instant;
import java.util.UUID;

/**
 * Representacion de salida de una Task, con nombres de campo snake_case
 * segun docs/api/components/schemas/task.yaml#/Task.
 */
public record TaskResponse(
        @JsonProperty("id") UUID id,
        @JsonProperty("user_story_id") UUID userStoryId,
        @JsonProperty("status_id") UUID statusId,
        @JsonProperty("assignee_id") UUID assigneeId,
        @JsonProperty("title") String title,
        @JsonProperty("created_at") Instant createdAt) {

    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getUserStoryId(),
                task.getStatusId(),
                task.getAssigneeId(),
                task.getTitle(),
                task.getCreatedAt());
    }
}
