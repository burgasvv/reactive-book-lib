package org.burgas.identityservice.handler;

import lombok.RequiredArgsConstructor;
import org.burgas.identityservice.dto.IdentityRequest;
import org.burgas.identityservice.dto.IdentityResponse;
import org.burgas.identityservice.service.IdentityService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class IdentityWebHandler {

    private final IdentityService identityService;

    public Mono<ServerResponse> handleFindAllIdentities(
            @SuppressWarnings("unused") ServerRequest serverRequest
    ) {
        return ServerResponse.ok().body(identityService.findAll(), IdentityResponse.class);
    }

    public Mono<ServerResponse> handleFindIdentityByUsername(ServerRequest serverRequest) {
        return ServerResponse.ok().body(
                identityService.findByUsername(serverRequest.pathVariable("username")),
                IdentityResponse.class
        );
    }

    public Mono<ServerResponse> handleCreateIdentity(ServerRequest serverRequest) {
        return ServerResponse.ok().body(
                identityService.create(serverRequest.bodyToMono(IdentityRequest.class)),
                IdentityResponse.class
        );
    }

    public Mono<ServerResponse> handleUpdateIdentity(ServerRequest serverRequest) {
        return ServerResponse.ok().body(
                identityService.update(serverRequest.bodyToMono(IdentityRequest.class)),
                IdentityResponse.class
        );
    }

    public Mono<ServerResponse> handleDeleteIdentity(ServerRequest serverRequest) {
        return ServerResponse.ok().body(
                identityService.delete(
                        serverRequest.queryParam("identityId").orElse(null)
                ),
                String.class
        );
    }
}
