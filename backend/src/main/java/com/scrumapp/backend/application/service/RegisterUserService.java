package com.scrumapp.backend.application.service;

import com.scrumapp.backend.adapter.in.web.ApiException;
import com.scrumapp.backend.adapter.in.web.ErrorTypes;
import com.scrumapp.backend.application.port.in.AuthTokens;
import com.scrumapp.backend.application.port.in.RegisterUserUseCase;
import com.scrumapp.backend.application.port.out.PasswordHasher;
import com.scrumapp.backend.application.port.out.RefreshTokenStore;
import com.scrumapp.backend.application.port.out.TokenProvider;
import com.scrumapp.backend.application.port.out.UserRepository;
import com.scrumapp.backend.domain.user.User;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenProvider tokenProvider;
    private final RefreshTokenStore refreshTokenStore;

    public RegisterUserService(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            TokenProvider tokenProvider,
            RefreshTokenStore refreshTokenStore) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenProvider = tokenProvider;
        this.refreshTokenStore = refreshTokenStore;
    }

    @Override
    public AuthTokens register(RegisterCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new ApiException(
                    ErrorTypes.CONFLICT,
                    "Email ya registrado",
                    HttpStatus.CONFLICT,
                    "Ya existe una cuenta con ese email.");
        }

        User user = new User(
                UUID.randomUUID(),
                command.email(),
                passwordHasher.hash(command.rawPassword()),
                command.firstNames(),
                command.lastNames(),
                true,
                Instant.now());
        User savedUser = userRepository.save(user);

        return AuthTokensIssuer.issue(savedUser.getId(), tokenProvider, refreshTokenStore);
    }
}
