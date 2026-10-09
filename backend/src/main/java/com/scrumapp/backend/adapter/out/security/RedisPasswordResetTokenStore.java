package com.scrumapp.backend.adapter.out.security;

import com.scrumapp.backend.application.port.out.PasswordResetTokenStore;
import java.time.Duration;
import java.util.Optional;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Implementacion con TTL en Redis: la key "pwd-reset-token:{token}" guarda el
 * email asociado y expira sola si nunca se confirma. consume() borra la key
 * de inmediato para que el token sea estrictamente de un solo uso.
 */
@Component
public class RedisPasswordResetTokenStore implements PasswordResetTokenStore {

    private static final String KEY_PREFIX = "pwd-reset-token:";

    private final StringRedisTemplate redisTemplate;

    public RedisPasswordResetTokenStore(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void store(String token, String email, Duration ttl) {
        redisTemplate.opsForValue().set(key(token), email, ttl);
    }

    @Override
    public Optional<String> consume(String token) {
        String key = key(token);
        String email = redisTemplate.opsForValue().get(key);
        if (email == null) {
            return Optional.empty();
        }
        redisTemplate.delete(key);
        return Optional.of(email);
    }

    private String key(String token) {
        return KEY_PREFIX + token;
    }
}
