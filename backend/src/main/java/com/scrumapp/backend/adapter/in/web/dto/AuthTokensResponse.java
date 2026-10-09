package com.scrumapp.backend.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scrumapp.backend.application.port.in.AuthTokens;

public record AuthTokensResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") long expiresIn) {

    public static AuthTokensResponse from(AuthTokens authTokens) {
        return new AuthTokensResponse(
                authTokens.accessToken(), authTokens.refreshToken(), authTokens.tokenType(), authTokens.expiresIn());
    }
}
