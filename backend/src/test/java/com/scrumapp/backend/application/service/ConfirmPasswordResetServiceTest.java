package com.scrumapp.backend.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scrumapp.backend.adapter.in.web.ApiException;
import com.scrumapp.backend.application.port.out.PasswordHasher;
import com.scrumapp.backend.application.port.out.PasswordResetTokenStore;
import com.scrumapp.backend.application.port.out.UserRepository;
import com.scrumapp.backend.domain.user.User;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConfirmPasswordResetServiceTest {

    @Mock
    private PasswordResetTokenStore passwordResetTokenStore;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordHasher passwordHasher;

    private ConfirmPasswordResetService service;

    @Test
    void updatesPasswordWhenTokenIsValid() {
        service = new ConfirmPasswordResetService(passwordResetTokenStore, userRepository, passwordHasher);

        User user = new User(UUID.randomUUID(), "ana@example.com", "old-hash", "Ana", "Gomez", true, Instant.now());
        when(passwordResetTokenStore.consume("valid-token")).thenReturn(Optional.of("ana@example.com"));
        when(userRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(user));
        when(passwordHasher.hash("N3wPassw0rd!")).thenReturn("new-hash");

        service.confirmReset("valid-token", "N3wPassw0rd!");

        verify(userRepository).save(user);
    }

    @Test
    void rejectsInvalidToken() {
        service = new ConfirmPasswordResetService(passwordResetTokenStore, userRepository, passwordHasher);

        when(passwordResetTokenStore.consume("invalid-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.confirmReset("invalid-token", "N3wPassw0rd!"))
                .isInstanceOf(ApiException.class);

        verify(userRepository, org.mockito.Mockito.never()).save(any());
    }
}
