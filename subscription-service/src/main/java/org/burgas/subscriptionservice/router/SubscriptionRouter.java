package org.burgas.subscriptionservice.router;

import lombok.RequiredArgsConstructor;
import org.burgas.subscriptionservice.handler.SubscriptionWebHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class SubscriptionRouter {

    private final SubscriptionWebHandler subscriptionWebHandler;

    @Bean
    public RouterFunction<ServerResponse> getSubscriptionsByIdentityId() {
        return RouterFunctions.route(
                RequestPredicates.GET("/subscriptions/identity/{identity-id}"),
                subscriptionWebHandler::handleFindByIdentityId
        );
    }

    @Bean
    public RouterFunction<ServerResponse> getSubscriptionById() {
        return RouterFunctions.route(
                RequestPredicates.GET("/subscriptions/{subscription-id}"),
                subscriptionWebHandler::handleFindById
        );
    }

    @Bean
    public RouterFunction<ServerResponse> createSubscription() {
        return RouterFunctions.route(
                RequestPredicates.POST("/subscriptions/create"),
                subscriptionWebHandler::handleCreateSubscription
        );
    }

    @Bean
    public RouterFunction<ServerResponse> updateSubscription() {
        return RouterFunctions.route(
                RequestPredicates.PUT("/subscriptions/edit"),
                subscriptionWebHandler::handleUpdateSubscription
        );
    }

    @Bean
    public RouterFunction<ServerResponse> addBookToSubscription() {
        return RouterFunctions.route(
                RequestPredicates.POST("/subscriptions/add-book"),
                subscriptionWebHandler::handleAddBookToSubscription
        );
    }
}
