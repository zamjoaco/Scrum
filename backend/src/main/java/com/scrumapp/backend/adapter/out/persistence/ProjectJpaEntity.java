package com.scrumapp.backend.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad de persistencia de Project. Mapea la tabla projects creada por la
 * migracion V3__create_projects_table.sql. Nunca se expone fuera de este
 * subpaquete.
 */
@Entity
@Table(name = "projects")
public class ProjectJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "workspace_id", nullable = false, updatable = false)
    private UUID workspaceId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "key", nullable = false)
    private String key;

    @Column(name = "description")
    private String description;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected ProjectJpaEntity() {
        // Requerido por JPA.
    }

    public ProjectJpaEntity(
            UUID id,
            UUID workspaceId,
            String name,
            String key,
            String description,
            LocalDateTime archivedAt,
            LocalDateTime createdAt) {
        this.id = id;
        this.workspaceId = workspaceId;
        this.name = name;
        this.key = key;
        this.description = description;
        this.archivedAt = archivedAt;
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

    public UUID getWorkspaceId() {
        return workspaceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
