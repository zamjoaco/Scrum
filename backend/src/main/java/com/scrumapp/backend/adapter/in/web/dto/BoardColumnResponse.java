package com.scrumapp.backend.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scrumapp.backend.domain.board.BoardColumn;
import java.util.List;
import java.util.UUID;

/**
 * Representacion de salida de una BoardColumn, con nombres de campo
 * snake_case. story_ids solo lleva los ids de las UserStory en la columna.
 */
public record BoardColumnResponse(
        @JsonProperty("id") UUID id,
        @JsonProperty("board_id") UUID boardId,
        @JsonProperty("name") String name,
        @JsonProperty("order_index") int orderIndex,
        @JsonProperty("wip_limit") Integer wipLimit,
        @JsonProperty("story_ids") List<UUID> storyIds) {

    public static BoardColumnResponse from(BoardColumn column, List<UUID> storyIds) {
        return new BoardColumnResponse(
                column.getId(),
                column.getBoardId(),
                column.getName(),
                column.getOrderIndex(),
                column.getWipLimit(),
                storyIds);
    }

    public static BoardColumnResponse from(BoardColumn column) {
        return from(column, List.of());
    }
}
