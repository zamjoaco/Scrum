package com.scrumapp.backend.domain.user;

import java.util.UUID;

public class UserNotFoundException extends DomainException {

    public UserNotFoundException(UUID userId) {
        super("No existe un usuario con id " + userId);
    }

    public UserNotFoundException(String email) {
        super("No existe un usuario con email " + email);
    }
}
