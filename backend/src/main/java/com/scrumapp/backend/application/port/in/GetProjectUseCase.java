package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.project.Project;
import java.util.UUID;

/**
 * Caso de uso: obtener un proyecto por id.
 */
public interface GetProjectUseCase {

    Project getProject(UUID projectId, UUID requesterUserId);
}
