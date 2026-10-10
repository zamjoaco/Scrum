package com.scrumapp.backend.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scrumapp.backend.application.port.in.BoardView;
import java.util.List;
import java.util.UUID;

/**
 * Representacion de salida de un Board, con sus columnas ordenadas y, por
 * cada columna, los ids de las UserStory que estan en ella.
 */
public record BoardResponse(
        @JsonProperty("id") UUID id,
        @JsonProperty("project_id") UUID projectId,
        @JsonProperty("name") String name,
        @JsonProperty("columns") List<BoardColumnResponse> columns) {

    public static BoardResponse from(BoardView view) {
        List<BoardColumnResponse> columns = view.columns().stream()
                .map(columnView -> BoardColumnResponse.from(columnView.column(), columnView.storyIds()))
                .toList();
        return new BoardResponse(
                view.board().getId(), view.board().getProjectId(), view.board().getName(), columns);
    }
}
