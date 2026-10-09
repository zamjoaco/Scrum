package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.workspace.Workspace;
import java.util.UUID;

/**
 * Caso de uso: actualizar el nombre de un workspace. Requiere que el
 * solicitante tenga rol OWNER o ADMIN.
 */
public interface UpdateWorkspaceUseCase {

    Workspace updateWorkspace(UUID workspaceId, UUID requesterId, String name);
}
