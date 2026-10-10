package com.scrumapp.backend.adapter.out.persistence;

import com.scrumapp.backend.application.port.out.BoardRepository;
import com.scrumapp.backend.domain.board.Board;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Adaptador de persistencia que implementa el puerto BoardRepository.
 * Traduce explicitamente entre la entidad de dominio Board y la entidad JPA
 * BoardJpaEntity; esta ultima no sale nunca de este subpaquete.
 */
@Repository
public class BoardPersistenceAdapter implements BoardRepository {

    private final BoardJpaRepository jpaRepository;

    public BoardPersistenceAdapter(BoardJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Board> findById(UUID id) {
        return jpaRepository.findById(id).map(BoardPersistenceAdapter::toDomain);
    }

    @Override
    public Optional<Board> findByProjectId(UUID projectId) {
        return jpaRepository.findByProjectId(projectId).map(BoardPersistenceAdapter::toDomain);
    }

    @Override
    public Board save(Board board) {
        BoardJpaEntity saved = jpaRepository.save(toJpaEntity(board));
        return toDomain(saved);
    }

    private static BoardJpaEntity toJpaEntity(Board board) {
        return new BoardJpaEntity(board.getId(), board.getProjectId(), board.getName());
    }

    private static Board toDomain(BoardJpaEntity entity) {
        return new Board(entity.getId(), entity.getProjectId(), entity.getName());
    }
}
