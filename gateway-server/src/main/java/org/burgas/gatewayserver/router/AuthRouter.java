package org.burgas.gatewayserver.router;

import lombok.RequiredArgsConstructor;
import org.burgas.gatewayserver.dto.IdentityPrincipal;
import org.burgas.gatewayserver.dto.IdentityResponse;
import org.burgas.gatewayserver.mapper.IdentityMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class AuthRouter {

    private final IdentityMapper identityMapper;

    @Bean
    public RouterFunction<ServerResponse> getPrincipal() {
        return route(
                GET("/auth/principal"),

                _ -> ServerResponse.ok().body(
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
                )
        );
    }
}
