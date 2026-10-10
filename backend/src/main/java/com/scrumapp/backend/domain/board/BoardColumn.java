package com.scrumapp.backend.domain.board;

import java.util.UUID;

/**
 * Entidad de dominio pura. No lleva anotaciones de JPA ni de Spring:
 * la persistencia se resuelve en adapter.out.persistence.
 */
public class BoardColumn {

    private final UUID id;
    private final UUID boardId;
    private String name;
    private int orderIndex;
    private Integer wipLimit;

    public BoardColumn(UUID id, UUID boardId, String name, int orderIndex, Integer wipLimit) {
        this.id = id;
        this.boardId = boardId;
        this.name = name;
        this.orderIndex = orderIndex;
        this.wipLimit = wipLimit;
    }

    public UUID getId() {
        return id;
    }

    public UUID getBoardId() {
        return boardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Integer getWipLimit() {
        return wipLimit;
    }

    public void setWipLimit(Integer wipLimit) {
        this.wipLimit = wipLimit;
    }
}
