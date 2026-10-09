package com.scrumapp.backend.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scrumapp.backend.domain.user.User;
import java.time.Instant;
import java.util.UUID;

/**
 * Representacion de salida de un usuario, con nombres de campo snake_case
 * segun docs/api/components/schemas/user.yaml#/User.
 */
public record UserResponse(
        @JsonProperty("id") UUID id,
        @JsonProperty("email") String email,
        @JsonProperty("first_names") String firstNames,
        @JsonProperty("last_names") String lastNames,
        @JsonProperty("is_active") boolean active,
        @JsonProperty("created_at") Instant createdAt) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstNames(),
                user.getLastNames(),
                user.isActive(),
                user.getCreatedAt());
    }
}
