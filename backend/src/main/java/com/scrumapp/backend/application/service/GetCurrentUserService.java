package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.GetCurrentUserUseCase;
import com.scrumapp.backend.application.port.out.UserRepository;
import com.scrumapp.backend.domain.user.User;
import com.scrumapp.backend.domain.user.UserNotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetCurrentUserService implements GetCurrentUserUseCase {

    private final UserRepository userRepository;

    public GetCurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getCurrentUser(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }
}
