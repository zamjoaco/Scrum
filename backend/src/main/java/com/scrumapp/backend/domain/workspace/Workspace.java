package com.scrumapp.backend.domain.workspace;

import java.time.Instant;
import java.util.UUID;

/**
 * Entidad de dominio pura. No lleva anotaciones de JPA ni de Spring:
 * la persistencia se resuelve en adapter.out.persistence.
 */
public class Workspace {

    private final UUID id;
    private String name;
    private String slug;
    private final UUID ownerId;
    private final Instant createdAt;

    public Workspace(UUID id, String name, String slug, UUID ownerId, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
