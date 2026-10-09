package com.scrumapp.backend.adapter.out.persistence;

import com.scrumapp.backend.domain.user.Role;
import com.scrumapp.backend.domain.workspace.Invitation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad de persistencia de invitacion a workspace. Mapea la tabla
 * invitations creada por la migracion V2__create_workspaces_tables.sql.
 * Nunca se expone fuera de este subpaquete.
 */
@Entity
@Table(name = "invitations")
public class InvitationJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "workspace_id", nullable = false, updatable = false)
    private UUID workspaceId;

    @Column(name = "email", nullable = false, updatable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "token", nullable = false, updatable = false, unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Invitation.Status status;

    @Column(name = "expires_at", nullable = false, updatable = false)
    private LocalDateTime expiresAt;

    protected InvitationJpaEntity() {
        // Requerido por JPA.
    }

    public InvitationJpaEntity(
            UUID id,
            UUID workspaceId,
            String email,
            Role role,
            String token,
            Invitation.Status status,
            LocalDateTime expiresAt) {
        this.id = id;
        this.workspaceId = workspaceId;
        this.email = email;
        this.role = role;
        this.token = token;
        this.status = status;
        this.expiresAt = expiresAt;
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

    public Invitation.Status getStatus() {
        return status;
    }

    public void setStatus(Invitation.Status status) {
        this.status = status;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
}
