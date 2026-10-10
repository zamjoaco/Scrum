package com.scrumapp.backend.domain.sprint;

import com.scrumapp.backend.domain.user.DomainException;
import java.util.UUID;

/**
 * Se lanza cuando se intenta iniciar o cerrar un sprint en un estado
 * invalido, o cuando ya existe otro sprint ACTIVE en el mismo proyecto.
 */
public class InvalidSprintTransitionException extends DomainException {

    public InvalidSprintTransitionException(String message) {
        super(message);
    }

    public static InvalidSprintTransitionException alreadyActiveSprint(
            UUID projectId, UUID activeSprintId) {
        return new InvalidSprintTransitionException(
                "El project " + projectId + " ya tiene un sprint activo: " + activeSprintId);
    }
}
