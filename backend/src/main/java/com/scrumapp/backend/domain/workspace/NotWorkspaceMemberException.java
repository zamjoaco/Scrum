package com.scrumapp.backend.domain.workspace;

import com.scrumapp.backend.domain.user.DomainException;
import java.util.UUID;

/**
 * Se lanza cuando un usuario intenta acceder a un workspace del que no es miembro.
 */
public class NotWorkspaceMemberException extends DomainException {

    public NotWorkspaceMemberException(UUID userId, UUID workspaceId) {
        super("El usuario " + userId + " no es miembro del workspace " + workspaceId);
    }
}
