package org.burgas.subscriptionservice.handler;

import lombok.RequiredArgsConstructor;
import org.burgas.subscriptionservice.dto.SubscriptionRequest;
import org.burgas.subscriptionservice.dto.SubscriptionResponse;
import org.burgas.subscriptionservice.service.SubscriptionService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class SubscriptionWebHandler {

    private final SubscriptionService subscriptionService;

    public Mono<ServerResponse> handleFindByIdentityId(ServerRequest request) {
        String authValue = request.headers().firstHeader(AUTHORIZATION);
        return ServerResponse.ok().body(
                subscriptionService.findByIdentityId(request.pathVariable("identity-id"), authValue),
                SubscriptionResponse.class
        );
    }

    public Mono<ServerResponse> handleFindById(ServerRequest request) {
        String authValue = request.headers().firstHeader(AUTHORIZATION);
        return ServerResponse.ok().body(
                subscriptionService.findById(request.pathVariable("subscription-id"), authValue),
                SubscriptionResponse.class
        );
    }

    public Mono<ServerResponse> handleCreateSubscription(ServerRequest request) {
        String authValue = request.headers().firstHeader(AUTHORIZATION);
        return ServerResponse.ok().body(
                subscriptionService.create(request.bodyToMono(SubscriptionRequest.class), authValue),
                SubscriptionResponse.class
        );
    }

    public Mono<ServerResponse> handleUpdateSubscription(ServerRequest request) {
        String authValue = request.headers().firstHeader(AUTHORIZATION);
        return ServerResponse.ok().body(
                subscriptionService.update(request.bodyToMono(SubscriptionRequest.class), authValue),
                SubscriptionResponse.class
        );
    }
}
