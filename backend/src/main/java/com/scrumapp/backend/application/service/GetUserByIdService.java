package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.GetUserByIdUseCase;
import com.scrumapp.backend.application.port.out.UserRepository;
import com.scrumapp.backend.domain.user.User;
import com.scrumapp.backend.domain.user.UserNotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetUserByIdService implements GetUserByIdUseCase {

    private final UserRepository userRepository;

    public GetUserByIdService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUserById(UUID requesterId, UUID targetUserId) {
        // TODO: cuando exista el modulo de Workspace, agregar la regla de
        // autorizacion "solo visible si comparte workspace" entre requesterId
        // y targetUserId. Por ahora se devuelve el usuario si existe.
        return userRepository
                .findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException(targetUserId));
    }
}
