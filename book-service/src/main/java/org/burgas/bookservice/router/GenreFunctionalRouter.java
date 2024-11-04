package org.burgas.bookservice.router;

import lombok.RequiredArgsConstructor;
import org.burgas.bookservice.handler.GenreWebHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class GenreFunctionalRouter {

    private final GenreWebHandler genreWebHandler;

    @Bean
    public RouterFunction<ServerResponse> genreRoutes() {
        return RouterFunctions.route()
                .GET("/genres", genreWebHandler::handleFindAll)
                .GET("/genres/{genre-id}", genreWebHandler::handleFindById)
                .POST("/genres/create", genreWebHandler::handleCreateOrUpdate)
                .PUT("/genres/edit", genreWebHandler::handleCreateOrUpdate)
                .build();
    }
}
