package com.scrumapp.backend.domain.board;

import com.scrumapp.backend.domain.user.DomainException;
import java.util.UUID;

/**
 * Se lanza al intentar mover una historia o tarea a una BoardColumn que ya
 * alcanzo su wipLimit.
 */
public class WipLimitExceededException extends DomainException {

    public WipLimitExceededException(UUID boardColumnId, int wipLimit) {
        super("La board column " + boardColumnId + " alcanzo su limite de wip (" + wipLimit + ")");
    }
}
