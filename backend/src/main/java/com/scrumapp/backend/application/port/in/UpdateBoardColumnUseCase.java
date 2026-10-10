package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.board.BoardColumn;
import java.util.UUID;

/**
 * Caso de uso: actualizar name/orderIndex/wipLimit de una BoardColumn
 * existente.
 */
public interface UpdateBoardColumnUseCase {

    BoardColumn updateBoardColumn(
            UUID columnId, UUID requesterUserId, String name, int orderIndex, Integer wipLimit);
}
