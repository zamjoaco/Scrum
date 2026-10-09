package com.scrumapp.backend.application.port.in;

import java.util.UUID;

public interface LogoutUseCase {

    void logout(UUID userId);
}
