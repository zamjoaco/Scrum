package com.scrumapp.backend.application.port.out;

import com.scrumapp.backend.domain.board.Board;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para persistencia de boards. Trabaja exclusivamente con
 * la entidad de dominio Board, nunca con una entidad JPA.
 */
public interface BoardRepository {

    Optional<Board> findById(UUID id);

    Optional<Board> findByProjectId(UUID projectId);

    Board save(Board board);
}
