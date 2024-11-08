package org.burgas.bookservice.handler;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.burgas.bookservice.dto.IdentityPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class WebClientHandler {

    private final WebClient webClient;

    @CircuitBreaker(
            name = "getPrincipal",
            fallbackMethod = "fallBackGetPrincipal"
    )
    public Mono<IdentityPrincipal> getPrincipal() {
        return webClient.get()
                .uri("http://localhost:8765/auth/principal")
                .retrieve()
                .bodyToMono(IdentityPrincipal.class);
    }

    @SuppressWarnings("unused")
    private Mono<IdentityPrincipal> fallBackGetPrincipal(Throwable throwable) {
        return Mono.just(IdentityPrincipal.builder().username("anonymous").isAuthenticated(false).build());
    }
}
