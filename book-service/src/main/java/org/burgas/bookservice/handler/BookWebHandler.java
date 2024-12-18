package org.burgas.bookservice.handler;

import lombok.RequiredArgsConstructor;
import org.burgas.bookservice.dto.BookRequest;
import org.burgas.bookservice.dto.BookResponse;
import org.burgas.bookservice.service.BookService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class BookWebHandler {

    private final BookService bookService;

    public Mono<ServerResponse> handleFindAll(@SuppressWarnings("unused") ServerRequest request) {
        return ServerResponse.ok().body(bookService.findAll(), BookResponse.class);
    }

    public Mono<ServerResponse> handleFindById(ServerRequest request) {
        return ServerResponse.ok().body(
                bookService.findById(request.pathVariable("book-id")), BookResponse.class
        );
    }

    public Mono<ServerResponse> handleFindBySubscriptionId(ServerRequest request) {
        return ServerResponse.ok().body(
                bookService.findBySubscriptionId(request.pathVariable("subscription-id")), BookResponse.class
        );
    }

    public Mono<ServerResponse> handleFindByGenreId(ServerRequest request) {
        return ServerResponse.ok().body(
                bookService.findByGenreId(request.pathVariable("genre-id")), BookResponse.class
        );
    }

    public Mono<ServerResponse> handleFindByAuthorId(ServerRequest request) {
        return ServerResponse.ok().body(
                bookService.findByAuthorId(request.pathVariable("author-id")), BookResponse.class
        );
    }

    public Mono<ServerResponse> handleCreate(ServerRequest request) {
        String authValue = request.headers().firstHeader(AUTHORIZATION);
        return ServerResponse.ok().body(
                bookService.createOrUpdate(request.bodyToMono(BookRequest.class), authValue),
                BookResponse.class
        );
    }
}
