package com.scrumapp.backend.application.port.in;

import java.util.UUID;

/**
 * Caso de uso: eliminar a un miembro de un workspace. Requiere que el
 * solicitante tenga rol OWNER o ADMIN.
 */
public interface RemoveMemberUseCase {

    void removeMember(UUID workspaceId, UUID requesterId, UUID targetUserId);
}
