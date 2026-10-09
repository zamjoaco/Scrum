package com.scrumapp.backend.adapter.in.web;

import com.scrumapp.backend.adapter.in.web.dto.AuthTokensResponse;
import com.scrumapp.backend.adapter.in.web.dto.LoginRequest;
import com.scrumapp.backend.adapter.in.web.dto.PasswordResetConfirmRequest;
import com.scrumapp.backend.adapter.in.web.dto.PasswordResetRequest;
import com.scrumapp.backend.adapter.in.web.dto.RefreshRequest;
import com.scrumapp.backend.adapter.in.web.dto.RegisterRequest;
import com.scrumapp.backend.application.port.in.ConfirmPasswordResetUseCase;
import com.scrumapp.backend.application.port.in.LoginUseCase;
import com.scrumapp.backend.application.port.in.LogoutUseCase;
import com.scrumapp.backend.application.port.in.RefreshTokenUseCase;
import com.scrumapp.backend.application.port.in.RegisterUserUseCase;
import com.scrumapp.backend.application.port.in.RegisterUserUseCase.RegisterCommand;
import com.scrumapp.backend.application.port.in.RequestPasswordResetUseCase;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final RequestPasswordResetUseCase requestPasswordResetUseCase;
    private final ConfirmPasswordResetUseCase confirmPasswordResetUseCase;

    public AuthController(
            RegisterUserUseCase registerUserUseCase,
            LoginUseCase loginUseCase,
            RefreshTokenUseCase refreshTokenUseCase,
            LogoutUseCase logoutUseCase,
            RequestPasswordResetUseCase requestPasswordResetUseCase,
            ConfirmPasswordResetUseCase confirmPasswordResetUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.logoutUseCase = logoutUseCase;
        this.requestPasswordResetUseCase = requestPasswordResetUseCase;
        this.confirmPasswordResetUseCase = confirmPasswordResetUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthTokensResponse> register(@Valid @RequestBody RegisterRequest request) {
        var tokens = registerUserUseCase.register(
                new RegisterCommand(request.firstNames(), request.lastNames(), request.email(), request.password()));
        return ResponseEntity.status(HttpStatus.CREATED).body(AuthTokensResponse.from(tokens));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokensResponse> login(@Valid @RequestBody LoginRequest request) {
        var tokens = loginUseCase.login(request.email(), request.password());
        return ResponseEntity.ok(AuthTokensResponse.from(tokens));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthTokensResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        var tokens = refreshTokenUseCase.refresh(request.refreshToken());
        return ResponseEntity.ok(AuthTokensResponse.from(tokens));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UUID userId) {
        logoutUseCase.logout(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password/reset-request")
    public ResponseEntity<Void> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
        requestPasswordResetUseCase.requestReset(request.email());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping("/password/reset-confirm")
    public ResponseEntity<Void> confirmPasswordReset(@Valid @RequestBody PasswordResetConfirmRequest request) {
        confirmPasswordResetUseCase.confirmReset(request.token(), request.newPassword());
        return ResponseEntity.noContent().build();
    }
}
