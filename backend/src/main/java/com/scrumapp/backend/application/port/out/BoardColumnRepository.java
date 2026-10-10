package com.scrumapp.backend.application.port.out;

import com.scrumapp.backend.domain.board.BoardColumn;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para persistencia de board columns. Trabaja
 * exclusivamente con la entidad de dominio BoardColumn, nunca con una
 * entidad JPA.
 */
public interface BoardColumnRepository {

    Optional<BoardColumn> findById(UUID id);

    BoardColumn save(BoardColumn boardColumn);

    /** Devuelve las columnas del board ordenadas por orderIndex ascendente. */
    List<BoardColumn> listByBoardId(UUID boardId);
}
