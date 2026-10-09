package com.scrumapp.backend.domain.project;

import com.scrumapp.backend.domain.user.DomainException;
import java.util.UUID;

public class ProjectNotFoundException extends DomainException {

    public ProjectNotFoundException(UUID projectId) {
        super("No existe un project con id " + projectId);
    }
}
