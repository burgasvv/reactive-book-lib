package org.burgas.bookservice.handler;

import lombok.RequiredArgsConstructor;
import org.burgas.bookservice.dto.GenreRequest;
import org.burgas.bookservice.dto.GenreResponse;
import org.burgas.bookservice.service.GenreService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class GenreWebHandler {

    private final GenreService genreService;

    public Mono<ServerResponse> handleFindAll(@SuppressWarnings("unused") ServerRequest request) {
        return ServerResponse.ok().body(genreService.findAll(), GenreResponse.class);
    }

    public Mono<ServerResponse> handleFindById(ServerRequest request) {
        return ServerResponse.ok().body(
                genreService.findById(request.pathVariable("genre-id")), GenreResponse.class
        );
    }

    public Mono<ServerResponse> handleCreateOrUpdate(ServerRequest request) {
        String authValue = request.headers().firstHeader(AUTHORIZATION);
        return ServerResponse.ok().body(
                genreService.createOrUpdate(request.bodyToMono(GenreRequest.class), authValue),
                GenreResponse.class
        );
    }
}
