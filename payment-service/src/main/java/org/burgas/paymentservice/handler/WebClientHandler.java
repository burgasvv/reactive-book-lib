package org.burgas.paymentservice.handler;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.burgas.paymentservice.dto.IdentityPrincipal;
import org.burgas.paymentservice.dto.SubscriptionRequest;
import org.burgas.paymentservice.dto.SubscriptionResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@RequiredArgsConstructor
public class WebClientHandler {

    private final WebClient webClient;

    public Mono<IdentityPrincipal> getPrincipal(String authValue) {
        return webClient.get()
                .uri(URI.create("http://localhost:8765/auth/principal"))
                .header(AUTHORIZATION, authValue)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(IdentityPrincipal.class));
    }

    @CircuitBreaker(
            name = "updateSubscription",
            fallbackMethod = "fallBackUpdatePrincipal"
    )
    public Mono<SubscriptionResponse> updateSubscription(
            Mono<SubscriptionRequest> subscriptionRequestMono, String authValue
    ) {
        return webClient.put()
                .uri("http://localhost:9010/subscriptions/edit")
                .body(subscriptionRequestMono, SubscriptionRequest.class)
                .header(AUTHORIZATION, authValue)
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .exchangeToMono(response -> response.bodyToMono(SubscriptionResponse.class));
    }

    @SuppressWarnings("unused")
    private Mono<SubscriptionResponse> fallBackUpdatePrincipal(Throwable throwable) {
        return Mono.error(throwable);
    }
}
