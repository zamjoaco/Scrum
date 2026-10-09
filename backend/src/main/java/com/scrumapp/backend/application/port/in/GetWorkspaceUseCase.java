package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.workspace.Workspace;
import java.util.UUID;

/**
 * Caso de uso: obtener un workspace por id. Requiere que el solicitante sea
 * miembro del workspace.
 */
public interface GetWorkspaceUseCase {

    Workspace getWorkspace(UUID workspaceId, UUID requesterId);
}
