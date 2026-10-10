package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.board.BoardColumn;
import java.util.UUID;

/**
 * Caso de uso: crear una BoardColumn nueva en el Board de un project.
 */
public interface CreateBoardColumnUseCase {

    BoardColumn createBoardColumn(
            UUID projectId, UUID requesterUserId, String name, int orderIndex, Integer wipLimit);
}
