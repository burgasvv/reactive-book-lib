package org.burgas.subscriptionservice.handler;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.burgas.subscriptionservice.dto.BookResponse;
import org.burgas.subscriptionservice.dto.IdentityPrincipal;
import org.burgas.subscriptionservice.dto.IdentityResponse;
import org.burgas.subscriptionservice.dto.SubscriptionResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
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
    public Mono<IdentityPrincipal> getPrincipal(String authValue) {
        return webClient.get()
                .uri("http://localhost:8765/auth/principal")
                .header(AUTHORIZATION, authValue)
                .retrieve()
                .bodyToMono(IdentityPrincipal.class);
    }

    @SuppressWarnings("unused")
    private Mono<IdentityPrincipal> fallBackGetPrincipal(Throwable throwable) {
        return Mono.just(IdentityPrincipal.builder().isAuthenticated(false).build());
    }

    @SuppressWarnings("unused")
    @CircuitBreaker(
            name = "getIdentityByUsername",
            fallbackMethod = "fallBackGetIdentityByUsername"
    )
    public Mono<IdentityResponse> getIdentityByUsername(String username, String authValue) {
        return webClient.get()
                .uri("http://localhost:8888/identities/{username}", username)
                .header(AUTHORIZATION, authValue)
                .retrieve()
                .bodyToMono(IdentityResponse.class);
    }

    @SuppressWarnings("unused")
    private Mono<IdentityResponse> fallBackGetIdentityByUsername(Throwable throwable) {
        return Mono.just(IdentityResponse.builder().build());
    }

    @CircuitBreaker(
            name = "getIdentityById",
            fallbackMethod = "fallBackGetIdentityById"
    )
    public Mono<IdentityResponse> getIdentityById(Long identityId, String authValue) {
        return webClient.get()
                .uri("http://localhost:8888/identities/identity/{identity-id}", identityId)
                .header(AUTHORIZATION, authValue)
                .retrieve()
                .bodyToMono(IdentityResponse.class);
    }

    @SuppressWarnings("unused")
    private Mono<IdentityResponse> fallBackGetIdentityById(Throwable throwable) {
        return Mono.just(IdentityResponse.builder().build());
    }

    @CircuitBreaker(
            name = "getBooksBySubscriptionId",
            fallbackMethod = "fallBackGetBooksBySubscriptionId"
    )
    public Flux<BookResponse> getBooksBySubscriptionId(Long subscriptionId, String authValue) {
        return webClient.get()
                .uri("http://localhost:9000/books/subscription/" + subscriptionId)
                .header(AUTHORIZATION, authValue)
                .retrieve()
                .bodyToFlux(BookResponse.class);
    }

    @SuppressWarnings("unused")
    public Mono<SubscriptionResponse> fallBackGetBooksBySubscriptionId(Throwable throwable) {
        return Mono.just(SubscriptionResponse.builder().build());
    }
}
