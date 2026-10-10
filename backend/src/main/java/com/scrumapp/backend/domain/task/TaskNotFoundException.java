package com.scrumapp.backend.domain.task;

import com.scrumapp.backend.domain.user.DomainException;
import java.util.UUID;

public class TaskNotFoundException extends DomainException {

    public TaskNotFoundException(UUID taskId) {
        super("No existe una task con id " + taskId);
    }
}
