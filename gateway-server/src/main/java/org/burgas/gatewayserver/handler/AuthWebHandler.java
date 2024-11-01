package org.burgas.gatewayserver.handler;

import lombok.RequiredArgsConstructor;
import org.burgas.gatewayserver.dto.IdentityPrincipal;
import org.burgas.gatewayserver.dto.IdentityResponse;
import org.burgas.gatewayserver.mapper.IdentityMapper;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthWebHandler {

    private final IdentityMapper identityMapper;

    public Mono<ServerResponse> getPrincipal(@SuppressWarnings("unused") ServerRequest request) {
        return ServerResponse.ok().body(
                ReactiveSecurityContextHolder.getContext()
                        .flatMap(
                                securityContext ->{
                                    if (securityContext.getAuthentication() != null &&
                                        securityContext.getAuthentication().isAuthenticated()) {

                                        IdentityResponse principal = (IdentityResponse) securityContext
                                                .getAuthentication().getPrincipal();
                                        return Mono.just(
                                                identityMapper.toIdentityPrincipal(principal, true)
                                        );

                                    } else
                                        return Mono.just(
                                                IdentityPrincipal.builder().isAuthenticated(false).build()
                                        );
                                }
                        ),

                IdentityPrincipal.class
        );
    }
}
