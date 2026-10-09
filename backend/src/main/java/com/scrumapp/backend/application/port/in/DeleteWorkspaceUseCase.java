package com.scrumapp.backend.application.port.in;

import java.util.UUID;

/**
 * Caso de uso: eliminar un workspace. Requiere que el solicitante tenga rol
 * OWNER.
 */
public interface DeleteWorkspaceUseCase {

    void deleteWorkspace(UUID workspaceId, UUID requesterId);
}
