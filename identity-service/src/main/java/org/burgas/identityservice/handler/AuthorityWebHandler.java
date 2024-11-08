package org.burgas.identityservice.handler;

import lombok.RequiredArgsConstructor;
import org.burgas.identityservice.dto.AuthorityRequest;
import org.burgas.identityservice.dto.AuthorityResponse;
import org.burgas.identityservice.service.AuthorityService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class AuthorityWebHandler {

    private final AuthorityService authorityService;

    public Mono<ServerResponse> handleFindAllAuthorities(
            @SuppressWarnings("unused") ServerRequest serverRequest
    ) {
        return ServerResponse.ok().body(authorityService.findAll(), AuthorityResponse.class);
    }

    public Mono<ServerResponse> handleFindAuthorityById(ServerRequest serverRequest) {
        return ServerResponse.ok().body(
                authorityService.findById(serverRequest.pathVariable("authority-id")),
                AuthorityResponse.class
        );
    }

    public Mono<ServerResponse> handleCreateOrUpdateAuthority(ServerRequest serverRequest) {
        String authValue = serverRequest.headers().firstHeader(AUTHORIZATION);
        return ServerResponse.ok().body(
                authorityService.createOrUpdate(serverRequest.bodyToMono(AuthorityRequest.class), authValue),
                AuthorityResponse.class
        );
    }
}
