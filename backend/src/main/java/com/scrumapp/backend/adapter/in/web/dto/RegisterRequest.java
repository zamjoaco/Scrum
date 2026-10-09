package com.scrumapp.backend.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @JsonProperty("first_names") @NotBlank String firstNames,
        @JsonProperty("last_names") @NotBlank String lastNames,
        @JsonProperty("email") @NotBlank @Email String email,
        @JsonProperty("password") @NotBlank @Size(min = 8) String password) {
}
