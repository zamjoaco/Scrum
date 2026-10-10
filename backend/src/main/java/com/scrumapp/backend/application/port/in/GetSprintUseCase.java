package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.sprint.Sprint;
import java.util.UUID;

/**
 * Caso de uso: obtener un sprint por id.
 */
public interface GetSprintUseCase {

    Sprint getSprint(UUID sprintId, UUID requesterUserId);
}
