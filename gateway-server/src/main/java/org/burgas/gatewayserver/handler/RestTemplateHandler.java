package org.burgas.gatewayserver.handler;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.burgas.gatewayserver.dto.IdentityResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RestTemplateHandler {

    private final RestTemplate restTemplate;

    @CircuitBreaker(
            name = "getIdentityByUsername",
            fallbackMethod = "fallBackGetIdentityByUsername"
    )
    public Mono<IdentityResponse> getIdentityByUsername(String username) {
        //noinspection BlockingMethodInNonBlockingContext
        return Mono.just(
                Objects.requireNonNull(
                        restTemplate.getForEntity(
                                URI.create("http://localhost:8888/identities/" + username),
                                IdentityResponse.class
                        )
                        .getBody()
                )
        );
    }

    @SuppressWarnings("unused")
    private Mono<IdentityResponse> fallBackGetIdentityByUsername(Throwable throwable) {
        return Mono.just(IdentityResponse.builder().build());
    }
}
