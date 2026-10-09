package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.project.Project;
import java.util.UUID;

/**
 * Caso de uso: archivar un proyecto (soft-delete). Setea archivedAt; nunca
 * borra la fila fisicamente.
 */
public interface ArchiveProjectUseCase {

    Project archiveProject(UUID projectId, UUID requesterUserId);
}
