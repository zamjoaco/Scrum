package com.scrumapp.backend.adapter.out.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio Spring Data JPA para UserStoryJpaEntity. Uso interno del
 * subpaquete de persistencia; la aplicacion depende de UserStoryRepository
 * (puerto de salida).
 */
public interface UserStoryJpaRepository extends JpaRepository<UserStoryJpaEntity, UUID> {

    List<UserStoryJpaEntity> findByProjectId(UUID projectId);

    long countByStatusId(UUID statusId);

    @Modifying
    @Query("UPDATE UserStoryJpaEntity s SET s.sprintId = NULL WHERE s.sprintId = :sprintId")
    void reassignSprintToNull(@Param("sprintId") UUID sprintId);
}
