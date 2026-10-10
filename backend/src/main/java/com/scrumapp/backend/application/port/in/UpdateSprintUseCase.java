package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.sprint.Sprint;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Caso de uso: actualizar name/goal/startDate/endDate de un sprint.
 */
public interface UpdateSprintUseCase {

    Sprint updateSprint(
            UUID sprintId,
            UUID requesterUserId,
            String name,
            String goal,
            LocalDate startDate,
            LocalDate endDate);
}
