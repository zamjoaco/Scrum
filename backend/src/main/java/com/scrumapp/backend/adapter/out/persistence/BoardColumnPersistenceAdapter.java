package com.scrumapp.backend.adapter.out.persistence;

import com.scrumapp.backend.application.port.out.BoardColumnRepository;
import com.scrumapp.backend.domain.board.BoardColumn;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Adaptador de persistencia que implementa el puerto BoardColumnRepository.
 * Traduce explicitamente entre la entidad de dominio BoardColumn y la
 * entidad JPA BoardColumnJpaEntity; esta ultima no sale nunca de este
 * subpaquete.
 */
@Repository
public class BoardColumnPersistenceAdapter implements BoardColumnRepository {

    private final BoardColumnJpaRepository jpaRepository;

    public BoardColumnPersistenceAdapter(BoardColumnJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<BoardColumn> findById(UUID id) {
        return jpaRepository.findById(id).map(BoardColumnPersistenceAdapter::toDomain);
    }

    @Override
    public BoardColumn save(BoardColumn boardColumn) {
        BoardColumnJpaEntity saved = jpaRepository.save(toJpaEntity(boardColumn));
        return toDomain(saved);
    }

    @Override
    public List<BoardColumn> listByBoardId(UUID boardId) {
        return jpaRepository.findByBoardIdOrderByOrderIndexAsc(boardId).stream()
                .map(BoardColumnPersistenceAdapter::toDomain)
                .toList();
    }

    private static BoardColumnJpaEntity toJpaEntity(BoardColumn boardColumn) {
        return new BoardColumnJpaEntity(
                boardColumn.getId(),
                boardColumn.getBoardId(),
                boardColumn.getName(),
                boardColumn.getOrderIndex(),
                boardColumn.getWipLimit());
    }

    private static BoardColumn toDomain(BoardColumnJpaEntity entity) {
        return new BoardColumn(
                entity.getId(),
                entity.getBoardId(),
                entity.getName(),
                entity.getOrderIndex(),
                entity.getWipLimit());
    }
}
