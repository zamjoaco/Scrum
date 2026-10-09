package com.scrumapp.backend.domain.workspace;

import com.scrumapp.backend.domain.user.DomainException;
import java.util.UUID;

/**
 * Se lanza cuando un usuario intenta una operacion de workspace que requiere
 * un rol (OWNER o ADMIN, o solo OWNER segun la operacion) que no tiene.
 */
public class InsufficientWorkspaceRoleException extends DomainException {

    public InsufficientWorkspaceRoleException(UUID userId, UUID workspaceId) {
        super("El usuario " + userId + " no tiene rol suficiente en el workspace " + workspaceId);
    }
}
