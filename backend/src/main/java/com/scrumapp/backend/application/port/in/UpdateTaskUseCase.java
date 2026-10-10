package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.task.Task;
import java.util.UUID;

/**
 * Caso de uso: actualizar una Task (merge patch). Cualquier parametro que
 * llegue en null significa "no modificar ese campo".
 */
public interface UpdateTaskUseCase {

    Task updateTask(
            UUID taskId, UUID requesterUserId, String title, UUID assigneeId, UUID statusId);
}
