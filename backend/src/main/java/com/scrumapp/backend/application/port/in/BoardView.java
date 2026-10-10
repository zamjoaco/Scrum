package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.board.Board;
import com.scrumapp.backend.domain.board.BoardColumn;
import java.util.List;
import java.util.UUID;

/**
 * Resultado de GetBoardUseCase: el Board, sus columnas ordenadas, y por cada
 * columna solo los ids de las UserStory que estan en ella (nunca el objeto
 * UserStory completo, para no duplicar responsabilidad con el worker de
 * Story).
 */
public record BoardView(Board board, List<ColumnView> columns) {

    public record ColumnView(BoardColumn column, List<UUID> storyIds) {
    }
}
