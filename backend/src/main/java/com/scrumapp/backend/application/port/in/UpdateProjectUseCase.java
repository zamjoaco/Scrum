package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.project.Project;
import java.util.UUID;

/**
 * Caso de uso: actualizar el nombre y la descripcion de un proyecto.
 */
public interface UpdateProjectUseCase {

    Project updateProject(UUID projectId, UUID requesterUserId, String name, String description);
}
