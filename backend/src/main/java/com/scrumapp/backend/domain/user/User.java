package com.scrumapp.backend.domain.user;

import java.time.Instant;
import java.util.UUID;

/**
 * Entidad de dominio pura. No lleva anotaciones de JPA ni de Spring:
 * la persistencia se resuelve en adapter.out.persistence.
 */
public class User {

    private final UUID id;
    private String email;
    private String passwordHash;
    private String firstNames;
    private String lastNames;
    private boolean isActive;
    private final Instant createdAt;

    public User(
            UUID id,
            String email,
            String passwordHash,
            String firstNames,
            String lastNames,
            boolean isActive,
            Instant createdAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstNames = firstNames;
        this.lastNames = lastNames;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFirstNames() {
        return firstNames;
    }

    public void setFirstNames(String firstNames) {
        this.firstNames = firstNames;
    }

    public String getLastNames() {
        return lastNames;
    }

    public void setLastNames(String lastNames) {
        this.lastNames = lastNames;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
