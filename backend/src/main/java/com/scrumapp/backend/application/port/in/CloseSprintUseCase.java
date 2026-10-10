package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.sprint.Sprint;
import java.util.UUID;

/**
 * Caso de uso: cerrar un sprint ACTIVE, dejandolo CLOSED. Falla si el sprint
 * no esta ACTIVE. Las historias que no quedaron en la columna final del
 * board vuelven al backlog (sprintId = null).
 */
public interface CloseSprintUseCase {

    Sprint closeSprint(UUID sprintId, UUID requesterUserId);
}
