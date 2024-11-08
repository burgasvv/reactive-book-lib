package org.burgas.subscriptionservice.handler;

import lombok.RequiredArgsConstructor;
import org.burgas.subscriptionservice.dto.PaymentRequest;
import org.burgas.subscriptionservice.dto.SubscriptionRequest;
import org.burgas.subscriptionservice.dto.SubscriptionResponse;
import org.burgas.subscriptionservice.service.SubscriptionService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SubscriptionWebHandler {

    private final SubscriptionService subscriptionService;

    public Mono<ServerResponse> handleFindByIdentityId(ServerRequest request) {
        return ServerResponse.ok().body(
                subscriptionService.findByIdentityId(request.pathVariable("identity-id")),
                SubscriptionResponse.class
        );
    }

    public Mono<ServerResponse> handleFindById(ServerRequest request) {
        return ServerResponse.ok().body(
                subscriptionService.findById(request.pathVariable("subscription-id"))
                        .log("SUBSCRIPTION-HANDLER"),
                SubscriptionResponse.class
        );
    }

    public Mono<ServerResponse> handleCreateSubscription(ServerRequest request) {
        return ServerResponse.ok().body(
                subscriptionService.create(request.bodyToMono(SubscriptionRequest.class)),
                SubscriptionResponse.class
        );
    }

    public Mono<ServerResponse> handleUpdateSubscription(ServerRequest request) {
        return ServerResponse.ok().body(
                subscriptionService.updateAfterPayment(request.bodyToMono(PaymentRequest.class)),
                SubscriptionResponse.class
        );
    }

    public Mono<ServerResponse> handleAddBookToSubscription(ServerRequest request) {
        return ServerResponse.ok().body(
                subscriptionService.addBookToSubscription(
                        request.bodyToMono(SubscriptionRequest.class),
                        request.queryParam("bookId").orElse(null)
                ),
                String.class
        );
    }
}
