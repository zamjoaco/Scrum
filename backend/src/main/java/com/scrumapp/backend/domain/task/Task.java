package com.scrumapp.backend.domain.task;

import java.time.Instant;
import java.util.UUID;

/**
 * Entidad de dominio pura. No lleva anotaciones de JPA ni de Spring:
 * la persistencia se resuelve en adapter.out.persistence.
 *
 * <p>Esta Task es una entidad de dominio del proyecto Scrum (la subtarea de
 * una UserStory). No tiene ninguna relacion con las tareas de orquestacion
 * de agentes de Orca.
 */
public class Task {

    private final UUID id;
    private final UUID userStoryId;
    private UUID statusId;
    private UUID assigneeId;
    private String title;
    private final Instant createdAt;

    public Task(
            UUID id,
            UUID userStoryId,
            UUID statusId,
            UUID assigneeId,
            String title,
            Instant createdAt) {
        this.id = id;
        this.userStoryId = userStoryId;
        this.statusId = statusId;
        this.assigneeId = assigneeId;
        this.title = title;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserStoryId() {
        return userStoryId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
