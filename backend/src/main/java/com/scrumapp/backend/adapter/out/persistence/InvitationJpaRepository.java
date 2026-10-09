package com.scrumapp.backend.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data JPA para InvitationJpaEntity. Uso interno del
 * subpaquete de persistencia; la aplicacion depende de InvitationRepository
 * (puerto de salida).
 */
public interface InvitationJpaRepository extends JpaRepository<InvitationJpaEntity, UUID> {

    Optional<InvitationJpaEntity> findByToken(String token);
}
