package com.scrumapp.backend.domain.board;

import java.util.UUID;

/**
 * Entidad de dominio pura. No lleva anotaciones de JPA ni de Spring:
 * la persistencia se resuelve en adapter.out.persistence.
 */
public class Board {

    private final UUID id;
    private final UUID projectId;
    private String name;

    public Board(UUID id, UUID projectId, String name) {
        this.id = id;
        this.projectId = projectId;
        this.name = name;
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
