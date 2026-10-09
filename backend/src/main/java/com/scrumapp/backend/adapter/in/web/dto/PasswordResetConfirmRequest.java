package com.scrumapp.backend.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordResetConfirmRequest(
        @JsonProperty("token") @NotBlank String token,
        @JsonProperty("new_password") @NotBlank @Size(min = 8) String newPassword) {
}
