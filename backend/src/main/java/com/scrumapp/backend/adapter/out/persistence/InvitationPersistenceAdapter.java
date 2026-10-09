package com.scrumapp.backend.adapter.out.persistence;

import com.scrumapp.backend.application.port.out.InvitationRepository;
import com.scrumapp.backend.domain.workspace.Invitation;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * Adaptador de persistencia que implementa el puerto InvitationRepository.
 * Traduce explicitamente entre la entidad de dominio Invitation y la entidad
 * JPA InvitationJpaEntity; esta ultima no sale nunca de este subpaquete.
 */
@Repository
public class InvitationPersistenceAdapter implements InvitationRepository {

    private final InvitationJpaRepository jpaRepository;

    public InvitationPersistenceAdapter(InvitationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Invitation save(Invitation invitation) {
        InvitationJpaEntity saved = jpaRepository.save(toJpaEntity(invitation));
        return toDomain(saved);
    }

    @Override
    public Optional<Invitation> findByToken(String token) {
        return jpaRepository.findByToken(token).map(InvitationPersistenceAdapter::toDomain);
    }

    @Override
    public void delete(Invitation invitation) {
        jpaRepository.deleteById(invitation.getId());
    }

    private static InvitationJpaEntity toJpaEntity(Invitation invitation) {
        return new InvitationJpaEntity(
                invitation.getId(),
                invitation.getWorkspaceId(),
                invitation.getEmail(),
                invitation.getRole(),
                invitation.getToken(),
                invitation.getStatus(),
                toLocalDateTime(invitation.getExpiresAt()));
    }

    private static Invitation toDomain(InvitationJpaEntity entity) {
        return new Invitation(
                entity.getId(),
                entity.getWorkspaceId(),
                entity.getEmail(),
                entity.getRole(),
                entity.getToken(),
                entity.getStatus(),
                toInstant(entity.getExpiresAt()));
    }

    private static LocalDateTime toLocalDateTime(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    private static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.toInstant(ZoneOffset.UTC);
    }
}
