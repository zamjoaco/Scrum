package com.scrumapp.backend.config;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import java.time.Duration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Backend de rate limiting distribuido con Bucket4j respaldado por Redis.
 * Usa una conexion Lettuce propia (separada del RedisTemplate de Spring Data)
 * porque el proxy manager de Bucket4j necesita manipular bytes crudos via el
 * protocolo CAS de Redis.
 */
@Configuration
public class RateLimiterConfig {

    @Bean(destroyMethod = "shutdown")
    public RedisClient rateLimiterRedisClient(RedisProperties redisProperties) {
        RedisURI.Builder uriBuilder = RedisURI.builder()
                .withHost(redisProperties.getHost())
                .withPort(redisProperties.getPort());
        if (redisProperties.getPassword() != null && !redisProperties.getPassword().isBlank()) {
            uriBuilder.withPassword(redisProperties.getPassword().toCharArray());
        }
        return RedisClient.create(uriBuilder.build());
    }

    @Bean
    public ProxyManager<String> rateLimiterProxyManager(RedisClient rateLimiterRedisClient) {
        StatefulRedisConnection<String, byte[]> connection = rateLimiterRedisClient.connect(
                RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));

        // maxTimeToLive debe ser mayor que la ventana mas larga configurada en
        // app.ratelimit (ver RateLimitProperties), para que Redis no expire el
        // bucket antes de que la ventana de rate limit termine.
        return LettuceBasedProxyManager.builderFor(connection)
                .withExpirationStrategy(
                        ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofDays(1)))
                .build();
    }
}
