package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.sprint.Sprint;
import java.util.UUID;

/**
 * Caso de uso: iniciar un sprint PLANNED, dejandolo ACTIVE. Falla si el
 * sprint no esta PLANNED o si el project ya tiene otro sprint ACTIVE.
 */
public interface StartSprintUseCase {

    Sprint startSprint(UUID sprintId, UUID requesterUserId);
}
