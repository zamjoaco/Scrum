package com.scrumapp.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scrumapp.backend.adapter.in.web.ApiException;
import com.scrumapp.backend.application.port.in.AuthTokens;
import com.scrumapp.backend.application.port.in.RegisterUserUseCase.RegisterCommand;
import com.scrumapp.backend.application.port.out.PasswordHasher;
import com.scrumapp.backend.application.port.out.RefreshTokenStore;
import com.scrumapp.backend.application.port.out.TokenProvider;
import com.scrumapp.backend.application.port.out.UserRepository;
import com.scrumapp.backend.domain.user.User;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegisterUserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordHasher passwordHasher;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private RefreshTokenStore refreshTokenStore;

    private RegisterUserService service;

    @Test
    void registersNewUserAndIssuesTokens() {
        service = new RegisterUserService(userRepository, passwordHasher, tokenProvider, refreshTokenStore);

        RegisterCommand command = new RegisterCommand("Ana", "Gomez", "ana@example.com", "S3cretPassw0rd!");
        when(userRepository.existsByEmail("ana@example.com")).thenReturn(false);
        when(passwordHasher.hash("S3cretPassw0rd!")).thenReturn("hashed");
        UUID userId = UUID.randomUUID();
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tokenProvider.generateAccessToken(any())).thenReturn("access-token");
        when(tokenProvider.accessTokenTtlSeconds()).thenReturn(900L);
        when(refreshTokenStore.ttl()).thenReturn(Duration.ofDays(7));

        AuthTokens tokens = service.register(command);

        assertThat(tokens.accessToken()).isEqualTo("access-token");
        assertThat(tokens.tokenType()).isEqualTo("Bearer");
        assertThat(tokens.expiresIn()).isEqualTo(900L);
        verify(userRepository).save(any(User.class));
        verify(refreshTokenStore).store(any(), any(), any());
    }

    @Test
    void rejectsDuplicateEmail() {
        service = new RegisterUserService(userRepository, passwordHasher, tokenProvider, refreshTokenStore);

        RegisterCommand command = new RegisterCommand("Ana", "Gomez", "ana@example.com", "S3cretPassw0rd!");
        when(userRepository.existsByEmail("ana@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.register(command)).isInstanceOf(ApiException.class);
    }
}
