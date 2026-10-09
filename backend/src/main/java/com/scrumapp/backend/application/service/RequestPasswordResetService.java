package com.scrumapp.backend.application.service;

import com.scrumapp.backend.adapter.in.web.ApiException;
import com.scrumapp.backend.adapter.in.web.ErrorTypes;
import com.scrumapp.backend.application.port.in.RequestPasswordResetUseCase;
import com.scrumapp.backend.application.port.out.PasswordResetTokenStore;
import com.scrumapp.backend.application.port.out.RateLimiter;
import com.scrumapp.backend.application.port.out.UserRepository;
import com.scrumapp.backend.config.RateLimitProperties;
import java.time.Duration;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class RequestPasswordResetService implements RequestPasswordResetUseCase {

    private static final String RATE_LIMIT_KEY_PREFIX = "pwd-reset:";

    /** Corta duracion a proposito: el token solo debe sobrevivir el tiempo que
     * tarda el usuario en revisar su email y confirmar el reset. */
    private static final Duration RESET_TOKEN_TTL = Duration.ofMinutes(15);

    private final UserRepository userRepository;
    private final PasswordResetTokenStore passwordResetTokenStore;
    private final RateLimiter rateLimiter;
    private final RateLimitProperties rateLimitProperties;

    public RequestPasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenStore passwordResetTokenStore,
            RateLimiter rateLimiter,
            RateLimitProperties rateLimitProperties) {
        this.userRepository = userRepository;
        this.passwordResetTokenStore = passwordResetTokenStore;
        this.rateLimiter = rateLimiter;
        this.rateLimitProperties = rateLimitProperties;
    }

    @Override
    public void requestReset(String email) {
        RateLimiter.Result rateLimitResult = rateLimiter.tryConsume(
                RATE_LIMIT_KEY_PREFIX + email,
                rateLimitProperties.resetMaxRequests(),
                rateLimitProperties.resetWindow());
        if (!rateLimitResult.allowed()) {
            throw new ApiException(
                    ErrorTypes.RATE_LIMITED,
                    "Demasiadas solicitudes de reset",
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Se supero el limite de solicitudes de reset de password para este email.",
                    rateLimitResult.retryAfterSeconds());
        }

        // No se revela si el email existe: si no hay usuario, simplemente no
        // se genera ningun token y el controller igual responde 202.
        userRepository.findByEmail(email).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            passwordResetTokenStore.store(token, email, RESET_TOKEN_TTL);
        });
    }
}
