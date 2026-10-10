package com.scrumapp.backend.application.port.in;

import java.util.UUID;

/** Caso de uso: borrar (hard delete) una Task. */
public interface DeleteTaskUseCase {

    void deleteTask(UUID taskId, UUID requesterUserId);
}
