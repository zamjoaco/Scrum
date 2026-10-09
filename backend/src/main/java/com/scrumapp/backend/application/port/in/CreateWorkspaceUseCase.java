package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.workspace.Workspace;
import java.util.UUID;

/**
 * Caso de uso: crear un workspace. El usuario creador queda como OWNER.
 */
public interface CreateWorkspaceUseCase {

    Workspace createWorkspace(UUID ownerId, String name);
}
