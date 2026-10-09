package com.scrumapp.backend.adapter.in.web;

import com.scrumapp.backend.adapter.in.web.dto.UpdateUserRequest;
import com.scrumapp.backend.adapter.in.web.dto.UserResponse;
import com.scrumapp.backend.application.port.in.GetCurrentUserUseCase;
import com.scrumapp.backend.application.port.in.GetUserByIdUseCase;
import com.scrumapp.backend.application.port.in.UpdateCurrentUserUseCase;
import com.scrumapp.backend.domain.user.User;
import com.scrumapp.backend.domain.user.UserNotFoundException;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador de entrada HTTP para el vertical slice de Users. Los tres endpoints
 * exigen autenticacion y quedan cubiertos por el default authenticated de
 * SecurityConfig. El userId autenticado se lee del principal expuesto por el
 * filtro de JWT (Authentication.getName()).
 */
@RestController
@RequestMapping("/users")
public class UsersController {

    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final UpdateCurrentUserUseCase updateCurrentUserUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;

    public UsersController(
            GetCurrentUserUseCase getCurrentUserUseCase,
            UpdateCurrentUserUseCase updateCurrentUserUseCase,
            GetUserByIdUseCase getUserByIdUseCase) {
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.updateCurrentUserUseCase = updateCurrentUserUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(Authentication authentication) {
        try {
            User user = getCurrentUserUseCase.getCurrentUser(authenticatedUserId(authentication));
            return UserResponse.from(user);
        } catch (UserNotFoundException ex) {
            throw unauthorized(ex);
        }
    }

    @PatchMapping("/me")
    public UserResponse updateCurrentUser(
            Authentication authentication, @Valid @RequestBody UpdateUserRequest request) {
        try {
            User user = updateCurrentUserUseCase.updateCurrentUser(
                    authenticatedUserId(authentication), request.firstNames(), request.lastNames());
            return UserResponse.from(user);
        } catch (UserNotFoundException ex) {
            throw unauthorized(ex);
        }
    }

    @GetMapping("/{user_id}")
    public UserResponse getUser(
            @PathVariable("user_id") UUID targetUserId, Authentication authentication) {
        try {
            User user = getUserByIdUseCase.getUserById(
                    authenticatedUserId(authentication), targetUserId);
            return UserResponse.from(user);
        } catch (UserNotFoundException ex) {
            throw new ApiException(
                    ErrorTypes.NOT_FOUND,
                    "Recurso no encontrado",
                    HttpStatus.NOT_FOUND,
                    ex.getMessage());
        }
    }

    private UUID authenticatedUserId(Authentication authentication) {
        return UUID.fromString(authentication.getName());
    }

    private ApiException unauthorized(UserNotFoundException ex) {
        return new ApiException(
                ErrorTypes.UNAUTHORIZED, "No autenticado", HttpStatus.UNAUTHORIZED, ex.getMessage());
    }
}
