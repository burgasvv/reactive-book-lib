package org.burgas.bookservice.router;

import lombok.RequiredArgsConstructor;
import org.burgas.bookservice.handler.AuthorWebHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class AuthorFunctionalRouter {

    private final AuthorWebHandler authorWebHandler;

    @Bean
    public RouterFunction<ServerResponse> authorRouter() {
        return RouterFunctions.route()
                .GET("/authors", authorWebHandler::handleFindAll)
                .GET("/authors/{author-id}", authorWebHandler::handleFindById)
                .POST("/authors/create", authorWebHandler::handleCreateOrUpdate)
                .PUT("/authors/edit", authorWebHandler::handleCreateOrUpdate)
                .build();
    }
}
