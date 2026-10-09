package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.workspace.Workspace;
import java.util.List;
import java.util.UUID;

/**
 * Caso de uso: listar los workspaces donde el usuario tiene membership.
 */
public interface ListWorkspacesUseCase {

    List<Workspace> listWorkspaces(UUID userId);
}
