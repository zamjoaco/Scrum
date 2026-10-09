package com.scrumapp.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.scrumapp.backend.adapter.in.web.ApiException;
import com.scrumapp.backend.application.port.in.AuthTokens;
import com.scrumapp.backend.application.port.out.PasswordHasher;
import com.scrumapp.backend.application.port.out.RateLimiter;
import com.scrumapp.backend.application.port.out.RefreshTokenStore;
import com.scrumapp.backend.application.port.out.TokenProvider;
import com.scrumapp.backend.application.port.out.UserRepository;
import com.scrumapp.backend.config.RateLimitProperties;
import com.scrumapp.backend.domain.user.InvalidCredentialsException;
import com.scrumapp.backend.domain.user.User;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordHasher passwordHasher;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private RefreshTokenStore refreshTokenStore;
    @Mock
    private RateLimiter rateLimiter;

    private final RateLimitProperties rateLimitProperties =
            new RateLimitProperties(5, Duration.ofMinutes(15), 3, Duration.ofHours(1));

    private LoginService service;

    @Test
    void loginSucceedsWithValidCredentials() {
        service = new LoginService(
                userRepository, passwordHasher, tokenProvider, refreshTokenStore, rateLimiter, rateLimitProperties);

        User user = new User(UUID.randomUUID(), "ana@example.com", "hashed", "Ana", "Gomez", true, Instant.now());
        when(rateLimiter.tryConsume(any(), anyLong(), any())).thenReturn(new RateLimiter.Result(true, 0));
        when(userRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("S3cretPassw0rd!", "hashed")).thenReturn(true);
        when(tokenProvider.generateAccessToken(user.getId())).thenReturn("access-token");
        when(tokenProvider.accessTokenTtlSeconds()).thenReturn(900L);
        when(refreshTokenStore.ttl()).thenReturn(Duration.ofDays(7));

        AuthTokens tokens = service.login("ana@example.com", "S3cretPassw0rd!");

        assertThat(tokens.accessToken()).isEqualTo("access-token");
    }

    @Test
    void rejectsInvalidPassword() {
        service = new LoginService(
                userRepository, passwordHasher, tokenProvider, refreshTokenStore, rateLimiter, rateLimitProperties);

        User user = new User(UUID.randomUUID(), "ana@example.com", "hashed", "Ana", "Gomez", true, Instant.now());
        when(rateLimiter.tryConsume(any(), anyLong(), any())).thenReturn(new RateLimiter.Result(true, 0));
        when(userRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("wrong", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> service.login("ana@example.com", "wrong"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void rejectsWhenRateLimitExceeded() {
        service = new LoginService(
                userRepository, passwordHasher, tokenProvider, refreshTokenStore, rateLimiter, rateLimitProperties);

        when(rateLimiter.tryConsume(any(), anyLong(), any())).thenReturn(new RateLimiter.Result(false, 30));

        assertThatThrownBy(() -> service.login("ana@example.com", "whatever"))
                .isInstanceOf(ApiException.class);
    }
}
