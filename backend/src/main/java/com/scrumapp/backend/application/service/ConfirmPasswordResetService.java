package com.scrumapp.backend.application.service;

import com.scrumapp.backend.adapter.in.web.ApiException;
import com.scrumapp.backend.adapter.in.web.ErrorTypes;
import com.scrumapp.backend.application.port.in.ConfirmPasswordResetUseCase;
import com.scrumapp.backend.application.port.out.PasswordHasher;
import com.scrumapp.backend.application.port.out.PasswordResetTokenStore;
import com.scrumapp.backend.application.port.out.UserRepository;
import com.scrumapp.backend.domain.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ConfirmPasswordResetService implements ConfirmPasswordResetUseCase {

    private final PasswordResetTokenStore passwordResetTokenStore;
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public ConfirmPasswordResetService(
            PasswordResetTokenStore passwordResetTokenStore,
            UserRepository userRepository,
            PasswordHasher passwordHasher) {
        this.passwordResetTokenStore = passwordResetTokenStore;
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public void confirmReset(String token, String newRawPassword) {
        String email = passwordResetTokenStore.consume(token)
                .orElseThrow(() -> new ApiException(
                        ErrorTypes.BAD_REQUEST,
                        "Token de reset invalido",
                        HttpStatus.BAD_REQUEST,
                        "El token de reset es invalido, ya fue usado, o expiro."));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(
                        ErrorTypes.BAD_REQUEST,
                        "Token de reset invalido",
                        HttpStatus.BAD_REQUEST,
                        "El token de reset es invalido, ya fue usado, o expiro."));

        user.setPasswordHash(passwordHasher.hash(newRawPassword));
        userRepository.save(user);
    }
}
