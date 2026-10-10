package com.scrumapp.backend.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad de persistencia de Task. Mapea la tabla tasks creada por la
 * migracion V8__create_tasks_table.sql. Nunca se expone fuera de este
 * subpaquete.
 */
@Entity
@Table(name = "tasks")
public class TaskJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_story_id", nullable = false, updatable = false)
    private UUID userStoryId;

    @Column(name = "status_id", nullable = false)
    private UUID statusId;

    @Column(name = "assignee_id")
    private UUID assigneeId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected TaskJpaEntity() {
        // Requerido por JPA.
    }

    public TaskJpaEntity(
            UUID id,
            UUID userStoryId,
            UUID statusId,
            UUID assigneeId,
            String title,
            LocalDateTime createdAt) {
        this.id = id;
        this.userStoryId = userStoryId;
        this.statusId = statusId;
        this.assigneeId = assigneeId;
        this.title = title;
        this.createdAt = createdAt;
    }

    @PrePersist
    void applyDefaultsIfMissing() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now(java.time.ZoneOffset.UTC);
        }
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
