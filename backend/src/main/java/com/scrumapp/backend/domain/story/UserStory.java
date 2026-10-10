package com.scrumapp.backend.domain.story;

import java.time.Instant;
import java.util.UUID;

/**
 * Entidad de dominio pura. No lleva anotaciones de JPA ni de Spring:
 * la persistencia se resuelve en adapter.out.persistence.
 */
public class UserStory {

    private final UUID id;
    private final UUID projectId;
    private UUID sprintId;
    private UUID statusId;
    private UUID assigneeId;
    private final UUID createdBy;
    private String title;
    private String description;
    private Integer storyPoints;
    private Priority priority;
    private final Instant createdAt;

    public UserStory(
            UUID id,
            UUID projectId,
            UUID sprintId,
            UUID statusId,
            UUID assigneeId,
            UUID createdBy,
            String title,
            String description,
            Integer storyPoints,
            Priority priority,
            Instant createdAt) {
        this.id = id;
        this.projectId = projectId;
        this.sprintId = sprintId;
        this.statusId = statusId;
        this.assigneeId = assigneeId;
        this.createdBy = createdBy;
        this.title = title;
        this.description = description;
        this.storyPoints = storyPoints;
        this.priority = priority;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public UUID getSprintId() {
        return sprintId;
    }

    public void setSprintId(UUID sprintId) {
        this.sprintId = sprintId;
    }

    public UUID getStatusId() {
        return statusId;
    }

    public void setStatusId(UUID statusId) {
        this.statusId = statusId;
    }

    public UUID getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(UUID assigneeId) {
        this.assigneeId = assigneeId;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(Integer storyPoints) {
        this.storyPoints = storyPoints;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
}
