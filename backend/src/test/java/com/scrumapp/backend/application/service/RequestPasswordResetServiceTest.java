package com.scrumapp.backend.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scrumapp.backend.adapter.in.web.ApiException;
import com.scrumapp.backend.application.port.out.PasswordResetTokenStore;
import com.scrumapp.backend.application.port.out.RateLimiter;
import com.scrumapp.backend.application.port.out.UserRepository;
import com.scrumapp.backend.config.RateLimitProperties;
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
class RequestPasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordResetTokenStore passwordResetTokenStore;
    @Mock
    private RateLimiter rateLimiter;

    private final RateLimitProperties rateLimitProperties =
            new RateLimitProperties(5, Duration.ofMinutes(15), 3, Duration.ofHours(1));

    private RequestPasswordResetService service;

    @Test
    void storesResetTokenWhenUserExists() {
        service = new RequestPasswordResetService(
                userRepository, passwordResetTokenStore, rateLimiter, rateLimitProperties);

        User user = new User(UUID.randomUUID(), "ana@example.com", "hashed", "Ana", "Gomez", true, Instant.now());
        when(rateLimiter.tryConsume(any(), anyLong(), any())).thenReturn(new RateLimiter.Result(true, 0));
        when(userRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(user));

        service.requestReset("ana@example.com");

        verify(passwordResetTokenStore).store(any(), eq("ana@example.com"), any());
    }

    @Test
    void doesNothingWhenUserDoesNotExist() {
        service = new RequestPasswordResetService(
                userRepository, passwordResetTokenStore, rateLimiter, rateLimitProperties);

        when(rateLimiter.tryConsume(any(), anyLong(), any())).thenReturn(new RateLimiter.Result(true, 0));
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        service.requestReset("missing@example.com");

        verify(passwordResetTokenStore, never()).store(any(), any(), any());
    }

    @Test
    void rejectsWhenRateLimitExceeded() {
        service = new RequestPasswordResetService(
                userRepository, passwordResetTokenStore, rateLimiter, rateLimitProperties);

        when(rateLimiter.tryConsume(any(), anyLong(), any())).thenReturn(new RateLimiter.Result(false, 60));

        assertThatThrownBy(() -> service.requestReset("ana@example.com")).isInstanceOf(ApiException.class);
    }
}
