package com.scrumapp.backend.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scrumapp.backend.domain.sprint.Sprint;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Representacion de salida de un Sprint, con nombres de campo snake_case.
 */
public record SprintResponse(
        @JsonProperty("id") UUID id,
        @JsonProperty("project_id") UUID projectId,
        @JsonProperty("name") String name,
        @JsonProperty("goal") String goal,
        @JsonProperty("start_date") LocalDate startDate,
        @JsonProperty("end_date") LocalDate endDate,
        @JsonProperty("status") Sprint.Status status) {

    public static SprintResponse from(Sprint sprint) {
        return new SprintResponse(
                sprint.getId(),
                sprint.getProjectId(),
                sprint.getName(),
                sprint.getGoal(),
                sprint.getStartDate(),
                sprint.getEndDate(),
                sprint.getStatus());
    }
}
