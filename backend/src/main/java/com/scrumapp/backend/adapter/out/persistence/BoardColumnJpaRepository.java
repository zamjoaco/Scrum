package com.scrumapp.backend.adapter.out.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data JPA para BoardColumnJpaEntity. Uso interno del
 * subpaquete de persistencia; la aplicacion depende de BoardColumnRepository
 * (puerto de salida).
 */
public interface BoardColumnJpaRepository extends JpaRepository<BoardColumnJpaEntity, UUID> {

    List<BoardColumnJpaEntity> findByBoardIdOrderByOrderIndexAsc(UUID boardId);
}
