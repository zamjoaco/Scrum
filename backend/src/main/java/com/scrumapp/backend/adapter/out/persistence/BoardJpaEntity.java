package com.scrumapp.backend.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * Entidad de persistencia de Board. Mapea la tabla boards creada por la
 * migracion V6__create_board_tables.sql. Nunca se expone fuera de este
 * subpaquete.
 */
@Entity
@Table(name = "boards")
public class BoardJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "project_id", nullable = false, updatable = false)
    private UUID projectId;

    @Column(name = "name", nullable = false)
    private String name;

    protected BoardJpaEntity() {
        // Requerido por JPA.
    }

    public BoardJpaEntity(UUID id, UUID projectId, String name) {
        this.id = id;
        this.projectId = projectId;
        this.name = name;
    }

    @PrePersist
    void applyDefaultsIfMissing() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
