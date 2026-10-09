package com.scrumapp.backend.domain.workspace;

import com.scrumapp.backend.domain.user.Role;
import java.time.Instant;
import java.util.UUID;

/**
 * Entidad de dominio pura. No lleva anotaciones de JPA ni de Spring:
 * la persistencia se resuelve en adapter.out.persistence.
 */
public class Invitation {

    public enum Status {
        PENDING,
        ACCEPTED,
        EXPIRED
    }

    private final UUID id;
    private final UUID workspaceId;
    private final String email;
    private Role role;
    private final String token;
    private Status status;
    private final Instant expiresAt;

    public Invitation(
            UUID id,
            UUID workspaceId,
            String email,
            Role role,
            String token,
            Status status,
            Instant expiresAt) {
        this.id = id;
        this.workspaceId = workspaceId;
        this.email = email;
        this.role = role;
        this.token = token;
        this.status = status;
        this.expiresAt = expiresAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getWorkspaceId() {
        return workspaceId;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
