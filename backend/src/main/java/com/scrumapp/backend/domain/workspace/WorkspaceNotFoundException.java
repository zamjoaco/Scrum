package com.scrumapp.backend.domain.workspace;

import com.scrumapp.backend.domain.user.DomainException;
import java.util.UUID;

public class WorkspaceNotFoundException extends DomainException {

    public WorkspaceNotFoundException(UUID workspaceId) {
        super("No existe un workspace con id " + workspaceId);
    }
}
