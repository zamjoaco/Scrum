package com.scrumapp.backend.domain.workspace;

import com.scrumapp.backend.domain.user.DomainException;

public class InvitationNotFoundException extends DomainException {

    public InvitationNotFoundException(String token) {
        super("No existe una invitacion con token " + token);
    }
}
