package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.UpdateCurrentUserUseCase;
import com.scrumapp.backend.application.port.out.UserRepository;
import com.scrumapp.backend.domain.user.User;
import com.scrumapp.backend.domain.user.UserNotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UpdateCurrentUserService implements UpdateCurrentUserUseCase {

    private final UserRepository userRepository;

    public UpdateCurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User updateCurrentUser(UUID userId, String firstNames, String lastNames) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        user.setFirstNames(firstNames);
        user.setLastNames(lastNames);
        return userRepository.save(user);
    }
}
