package com.scrumapp.backend.application.service;

import com.scrumapp.backend.adapter.in.web.ApiException;
import com.scrumapp.backend.adapter.in.web.ErrorTypes;
import com.scrumapp.backend.application.port.in.AuthTokens;
import com.scrumapp.backend.application.port.in.LoginUseCase;
import com.scrumapp.backend.application.port.out.PasswordHasher;
import com.scrumapp.backend.application.port.out.RateLimiter;
import com.scrumapp.backend.application.port.out.RefreshTokenStore;
import com.scrumapp.backend.application.port.out.TokenProvider;
import com.scrumapp.backend.application.port.out.UserRepository;
import com.scrumapp.backend.config.RateLimitProperties;
import com.scrumapp.backend.domain.user.InvalidCredentialsException;
import com.scrumapp.backend.domain.user.User;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class LoginService implements LoginUseCase {

    private static final String RATE_LIMIT_KEY_PREFIX = "login:";

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenProvider tokenProvider;
    private final RefreshTokenStore refreshTokenStore;
    private final RateLimiter rateLimiter;
    private final RateLimitProperties rateLimitProperties;

    public LoginService(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            TokenProvider tokenProvider,
            RefreshTokenStore refreshTokenStore,
            RateLimiter rateLimiter,
            RateLimitProperties rateLimitProperties) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenProvider = tokenProvider;
        this.refreshTokenStore = refreshTokenStore;
        this.rateLimiter = rateLimiter;
        this.rateLimitProperties = rateLimitProperties;
    }

    @Override
    public AuthTokens login(String email, String rawPassword) {
        RateLimiter.Result rateLimitResult = rateLimiter.tryConsume(
                RATE_LIMIT_KEY_PREFIX + email,
                rateLimitProperties.loginMaxFailures(),
                rateLimitProperties.loginWindow());
        if (!rateLimitResult.allowed()) {
            throw new ApiException(
                    ErrorTypes.RATE_LIMITED,
                    "Demasiados intentos de login",
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Se supero el limite de intentos de login para este email.",
                    rateLimitResult.retryAfterSeconds());
        }

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()
                || !passwordHasher.matches(rawPassword, user.get().getPasswordHash())
                || !user.get().isActive()) {
            throw new InvalidCredentialsException();
        }

        return AuthTokensIssuer.issue(user.get().getId(), tokenProvider, refreshTokenStore);
    }
}
