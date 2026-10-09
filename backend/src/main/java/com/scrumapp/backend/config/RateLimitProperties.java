package com.scrumapp.backend.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.ratelimit")
public record RateLimitProperties(
        long loginMaxFailures,
        Duration loginWindow,
        long resetMaxRequests,
        Duration resetWindow) {
}
