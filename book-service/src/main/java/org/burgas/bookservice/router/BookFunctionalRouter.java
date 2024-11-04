package org.burgas.bookservice.router;

import lombok.RequiredArgsConstructor;
import org.burgas.bookservice.handler.BookWebHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class BookFunctionalRouter {

    private final BookWebHandler bookWebHandler;

    @Bean
    public RouterFunction<ServerResponse> getAllBooks() {
        return route(GET("/books"), bookWebHandler::handleFindAll);
    }

    @Bean
    public RouterFunction<ServerResponse> getBookById() {
        return route(GET("/books/{book-id}"), bookWebHandler::handleFindById);
    }

    @Bean
    public RouterFunction<ServerResponse> getBooksBySubscriptionId() {
        return route(GET("/books/subscription/{subscription-id}"), bookWebHandler::handleFindBySubscriptionId);
    }

    @Bean
    public RouterFunction<ServerResponse> getBooksByGenreId() {
        return route(GET("/books/by-genre/{genre-id}"), bookWebHandler::handleFindByGenreId);
    }

    @Bean
    public RouterFunction<ServerResponse> getBooksByAuthorId() {
        return route(GET("/books/by-author/{author-id}"), bookWebHandler::handleFindByAuthorId);
    }

    @Bean
    public RouterFunction<ServerResponse> createBook() {
        return route(POST("/books/create"), bookWebHandler::handleCreate);
    }

    @Bean
    public RouterFunction<ServerResponse> updateBook() {
        return route(PUT("/books/edit"), bookWebHandler::handleCreate);
    }
}
