package org.burgas.identityservice.handler;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.burgas.identityservice.dto.IdentityPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class WebClientHandler {

    private final WebClient webClient;

    @CircuitBreaker(
            name = "getPrincipal",
            fallbackMethod = "fallBackGetPrincipal"
    )
    public Mono<IdentityPrincipal> getPrincipal(String authValue) {
        return webClient.get()
                .uri(URI.create("http://localhost:8765/auth/principal"))
                .header(AUTHORIZATION, authValue)
                .exchangeToMono(response -> response.bodyToMono(IdentityPrincipal.class));
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
