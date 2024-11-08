package org.burgas.bookservice.handler;

import lombok.RequiredArgsConstructor;
import org.burgas.bookservice.dto.AuthorRequest;
import org.burgas.bookservice.dto.AuthorResponse;
import org.burgas.bookservice.service.AuthorService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthorWebHandler {

    private final AuthorService authorService;

    public Mono<ServerResponse> handleFindAll(@SuppressWarnings("unused") ServerRequest request) {
        return ServerResponse.ok().body(authorService.findAll(), AuthorResponse.class);
    }

    public Mono<ServerResponse> handleFindById(ServerRequest request) {
        return ServerResponse.ok().body(
                authorService.findById(request.pathVariable("author-id")), AuthorResponse.class
        );
    }

    public Mono<ServerResponse> handleCreateOrUpdate(ServerRequest request) {
        return ServerResponse.ok().body(
                authorService.createOrUpdate(request.bodyToMono(AuthorRequest.class)),
                AuthorResponse.class
        );
    }
}
