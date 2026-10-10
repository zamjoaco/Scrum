package com.scrumapp.backend.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * Entidad de persistencia de BoardColumn. Mapea la tabla board_columns
 * creada por la migracion V6__create_board_tables.sql. Nunca se expone
 * fuera de este subpaquete.
 */
@Entity
@Table(name = "board_columns")
public class BoardColumnJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "board_id", nullable = false, updatable = false)
    private UUID boardId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    @Column(name = "wip_limit")
    private Integer wipLimit;

    protected BoardColumnJpaEntity() {
        // Requerido por JPA.
    }

    public BoardColumnJpaEntity(UUID id, UUID boardId, String name, int orderIndex, Integer wipLimit) {
        this.id = id;
        this.boardId = boardId;
        this.name = name;
        this.orderIndex = orderIndex;
        this.wipLimit = wipLimit;
    }

    @PrePersist
    void applyDefaultsIfMissing() {
        if (id == null) {
            id = UUID.randomUUID();
        }
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
