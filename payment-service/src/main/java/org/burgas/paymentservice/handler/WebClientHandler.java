package org.burgas.paymentservice.handler;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.burgas.paymentservice.dto.IdentityPrincipal;
import org.burgas.paymentservice.dto.PaymentRequest;
import org.burgas.paymentservice.dto.SubscriptionResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

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
    public Mono<SubscriptionResponse> updateSubscriptionAfterPayment(
            Mono<PaymentRequest> paymentRequestMono, String authValue
    ) {
        return webClient.put()
                .uri("http://localhost:9010/subscriptions/edit")
                .body(paymentRequestMono, PaymentRequest.class)
                .header(AUTHORIZATION, authValue)
                .exchangeToMono(response -> response.bodyToMono(SubscriptionResponse.class));
    }

    @SuppressWarnings("unused")
    private Mono<SubscriptionResponse> fallBackUpdatePrincipal(Throwable throwable) {
        return Mono.error(throwable);
    }

    @CircuitBreaker(
            name = "getSubscriptionById",
            fallbackMethod = "fallBackGetSubscriptionById"
    )
    public Mono<SubscriptionResponse> getSubscriptionById(Long subscriptionId, String authValue) {
        return webClient.get()
                .uri("http://localhost:9010/subscriptions/{subscription-id}", subscriptionId)
                .header(AUTHORIZATION, authValue)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(SubscriptionResponse.class));
    }

    @SuppressWarnings("unused")
    private Mono<SubscriptionResponse> fallBackGetSubscriptionById(Throwable throwable) {
        return Mono.just(SubscriptionResponse.builder().build());
    }
}
