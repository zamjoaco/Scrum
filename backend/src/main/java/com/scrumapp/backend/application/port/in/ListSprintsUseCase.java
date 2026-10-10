package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.sprint.Sprint;
import java.util.List;
import java.util.UUID;

/**
 * Caso de uso: listar los sprints de un project.
 */
public interface ListSprintsUseCase {

    List<Sprint> listSprints(UUID projectId, UUID requesterUserId);
}
