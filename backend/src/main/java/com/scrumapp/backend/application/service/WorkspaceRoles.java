package com.scrumapp.backend.application.service;

import com.scrumapp.backend.domain.user.Role;
import com.scrumapp.backend.domain.workspace.InsufficientWorkspaceRoleException;
import java.util.UUID;

/**
 * Chequeo de rol compartido por los casos de uso de workspace que exigen
 * OWNER o ADMIN para operar.
 */
final class WorkspaceRoles {

    static void requireOwnerOrAdmin(Role role, UUID userId, UUID workspaceId) {
        if (role != Role.OWNER && role != Role.ADMIN) {
            throw new InsufficientWorkspaceRoleException(userId, workspaceId);
        }
    }

    private WorkspaceRoles() {
    }
}
