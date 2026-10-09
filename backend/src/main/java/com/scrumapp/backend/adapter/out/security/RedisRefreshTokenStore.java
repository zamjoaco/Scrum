package com.scrumapp.backend.adapter.out.security;

import com.scrumapp.backend.application.port.out.RefreshTokenStore;
import com.scrumapp.backend.domain.user.InvalidCredentialsException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Cada usuario tiene a lo sumo un refresh token activo a la vez: la key es
 * "refresh-token:{userId}" y store() sobreescribe cualquier token previo
 * (equivalente a invalidar sesiones anteriores al emitir una nueva).
 */
@Component
public class RedisRefreshTokenStore implements RefreshTokenStore {

    private static final String KEY_PREFIX = "refresh-token:";

    private final StringRedisTemplate redisTemplate;
    private final Duration ttl;

    public RedisRefreshTokenStore(
            StringRedisTemplate redisTemplate,
            @Value("${app.refresh-token.ttl:7d}") Duration ttl) {
        this.redisTemplate = redisTemplate;
        this.ttl = ttl;
    }

    @Override
    public void store(UUID userId, String refreshToken, Instant expiresAt) {
        Duration remaining = Duration.between(Instant.now(), expiresAt);
        redisTemplate.opsForValue().set(key(userId), refreshToken, remaining.isNegative() ? Duration.ZERO : remaining);
    }

    @Override
    public String validateAndRotate(UUID userId, String refreshToken) {
        String storedToken = redisTemplate.opsForValue().get(key(userId));
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new InvalidCredentialsException();
        }

        String newSecret = UUID.randomUUID().toString();
        String rotatedToken = userId + ":" + newSecret;
        redisTemplate.opsForValue().set(key(userId), rotatedToken, ttl);
        return rotatedToken;
    }

    @Override
    public void revoke(UUID userId, String refreshToken) {
        redisTemplate.delete(key(userId));
    }

    @Override
    public Duration ttl() {
        return ttl;
    }

    private String key(UUID userId) {
        return KEY_PREFIX + userId;
    }
}
