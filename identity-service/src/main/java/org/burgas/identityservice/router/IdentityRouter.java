package org.burgas.identityservice.router;

import lombok.RequiredArgsConstructor;
import org.burgas.identityservice.handler.IdentityWebHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class IdentityRouter {

    private final IdentityWebHandler identityWebHandler;

    @Bean
    public RouterFunction<ServerResponse> getAllIdentities() {
        return RouterFunctions.route()
                .GET("/identities", identityWebHandler::handleFindAllIdentities)
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> getIdentityByUsername() {
        return RouterFunctions.route(
                RequestPredicates.GET("/identities/{username}"),
                identityWebHandler::handleFindIdentityByUsername
        );
    }

    @Bean
    public RouterFunction<ServerResponse> createIdentity() {
        return RouterFunctions.route()
                .POST("/identities/create", identityWebHandler::handleCreateIdentity)
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> updateIdentity() {
        return RouterFunctions.route(
                RequestPredicates.PUT("/identities/edit"), identityWebHandler::handleUpdateIdentity
        );
    }

    @Bean
    public RouterFunction<ServerResponse> deleteIdentity() {
        return RouterFunctions.route()
                .DELETE("/identities/delete", identityWebHandler::handleDeleteIdentity)
                .build();
    }
}
