package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.project.Project;
import java.util.UUID;

/**
 * Caso de uso: crear un proyecto dentro de un workspace. Cualquier rol de
 * membresia alcanza para crear un project (no se exige OWNER/ADMIN).
 */
public interface CreateProjectUseCase {

    Project createProject(
            UUID workspaceId, UUID requesterUserId, String name, String key, String description);
}
