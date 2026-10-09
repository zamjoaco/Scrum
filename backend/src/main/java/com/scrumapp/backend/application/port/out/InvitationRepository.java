package com.scrumapp.backend.application.port.out;

import com.scrumapp.backend.domain.workspace.Invitation;
import java.util.Optional;

/**
 * Puerto de salida para persistencia de invitaciones a workspace. Trabaja
 * exclusivamente con la entidad de dominio Invitation, nunca con una
 * entidad JPA.
 */
public interface InvitationRepository {

    Invitation save(Invitation invitation);

    Optional<Invitation> findByToken(String token);

    void delete(Invitation invitation);
}
