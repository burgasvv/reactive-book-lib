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

@Component
@RequiredArgsConstructor
public class WebClientHandler {

    private final WebClient webClient;

    public Mono<IdentityPrincipal> getPrincipal() {
        return webClient.get()
                .uri(URI.create("http://localhost:8765/auth/principal"))
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(IdentityPrincipal.class));
    }

    @CircuitBreaker(
            name = "updateSubscription",
            fallbackMethod = "fallBackUpdatePrincipal"
    )
    public Mono<SubscriptionResponse> updateSubscriptionAfterPayment(
            Mono<PaymentRequest> paymentRequestMono
    ) {
        return webClient.put()
                .uri("http://localhost:9010/subscriptions/edit")
                .body(paymentRequestMono, PaymentRequest.class)
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
    public Mono<SubscriptionResponse> getSubscriptionById(Long subscriptionId) {
        return webClient.get()
                .uri("http://localhost:9010/subscriptions/{subscription-id}", subscriptionId)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(SubscriptionResponse.class));
    }

    @SuppressWarnings("unused")
    private Mono<SubscriptionResponse> fallBackGetSubscriptionById(Throwable throwable) {
        return Mono.just(SubscriptionResponse.builder().build());
    }
}
