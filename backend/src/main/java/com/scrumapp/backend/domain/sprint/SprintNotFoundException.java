package com.scrumapp.backend.domain.sprint;

import com.scrumapp.backend.domain.user.DomainException;
import java.util.UUID;

public class SprintNotFoundException extends DomainException {

    public SprintNotFoundException(UUID sprintId) {
        super("No existe un sprint con id " + sprintId);
    }
}
