package com.scrumapp.backend.config;

import com.scrumapp.backend.application.port.out.RateLimiter;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import java.time.Duration;
import org.springframework.stereotype.Component;

@Component
public class Bucket4jRateLimiter implements RateLimiter {

    private final ProxyManager<String> proxyManager;

    public Bucket4jRateLimiter(ProxyManager<String> proxyManager) {
        this.proxyManager = proxyManager;
    }

    @Override
    public Result tryConsume(String key, long maxAttempts, Duration window) {
        BucketConfiguration configuration = BucketConfiguration.builder()
                .addLimit(Bandwidth.builder().capacity(maxAttempts).refillIntervally(maxAttempts, window).build())
                .build();

        Bucket bucket = proxyManager.builder().build(key, () -> configuration);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        long retryAfterSeconds = probe.isConsumed() ? 0 : Duration.ofNanos(probe.getNanosToWaitForRefill()).toSeconds();
        return new Result(probe.isConsumed(), retryAfterSeconds);
    }
}
