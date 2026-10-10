package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.sprint.Sprint;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Caso de uso: crear un sprint PLANNED para un project.
 */
public interface CreateSprintUseCase {

    Sprint createSprint(
            UUID projectId,
            UUID requesterUserId,
            String name,
            String goal,
            LocalDate startDate,
            LocalDate endDate);
}
