package com.scrumapp.backend.domain.workspace;

import com.scrumapp.backend.domain.user.DomainException;

public class InvitationExpiredException extends DomainException {

    public InvitationExpiredException(String token) {
        super("La invitacion con token " + token + " esta expirada");
    }
}
