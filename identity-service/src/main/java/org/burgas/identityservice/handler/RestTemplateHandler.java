package org.burgas.identityservice.handler;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.burgas.identityservice.dto.IdentityPrincipal;
import org.burgas.identityservice.interceptor.AuthenticationWebInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RestTemplateHandler {

    private final RestTemplate restTemplate;

    @CircuitBreaker(
            name = "getPrincipal",
            fallbackMethod = "fallBackGetPrincipal"
    )
    public Mono<IdentityPrincipal> getPrincipal(String authorization) {
        restTemplate.setInterceptors(
                List.of(new AuthenticationWebInterceptor(authorization))
        );
        //noinspection BlockingMethodInNonBlockingContext
        return Mono.just(
                Objects.requireNonNull(
                        restTemplate.getForObject(
                            URI.create("http://localhost:8765/auth/principal"),
                            IdentityPrincipal.class
                        )
                )
        );
    }

    @SuppressWarnings("unused")
    private Mono<IdentityPrincipal> fallBackGetPrincipal(Throwable throwable) {
        return Mono.just(
                IdentityPrincipal.builder()
                        .username("anonymous").isAuthenticated(false)
                        .build()
        );
    }
}
