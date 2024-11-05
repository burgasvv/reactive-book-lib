package org.burgas.identityservice.router;

import lombok.RequiredArgsConstructor;
import org.burgas.identityservice.handler.AuthorityWebHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class AuthorityWebRouter {

    private final AuthorityWebHandler authorityWebHandler;

    @Bean
    public RouterFunction<ServerResponse> getAllAuthorities() {
        return RouterFunctions.route(
                RequestPredicates.GET("/authorities"), authorityWebHandler::handleFindAllAuthorities
        );
    }

    @Bean
    public RouterFunction<ServerResponse> getAuthorityById() {
        return RouterFunctions.route()
                .GET("/authorities/{authority-id}", authorityWebHandler::handleFindAuthorityById)
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> createAuthority() {
        return RouterFunctions.route(
                RequestPredicates.POST("/authorities/create"),
                authorityWebHandler::handleCreateOrUpdateAuthority
        );
    }

    @Bean
    public RouterFunction<ServerResponse> updateAuthority() {
        return RouterFunctions.route()
                .PUT("/authorities/edit", authorityWebHandler::handleCreateOrUpdateAuthority)
                .build();
    }
}
