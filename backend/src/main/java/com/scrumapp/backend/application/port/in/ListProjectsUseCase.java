package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.project.Project;
import java.util.List;
import java.util.UUID;

/**
 * Caso de uso: listar los proyectos de un workspace.
 */
public interface ListProjectsUseCase {

    List<Project> listProjects(UUID workspaceId, UUID requesterUserId);
}
