package org.burgas.gatewayserver.router;

import lombok.RequiredArgsConstructor;
import org.burgas.gatewayserver.handler.AuthWebHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class AuthRouter {

    private final AuthWebHandler authWebHandler;

    @Bean
    public RouterFunction<ServerResponse> getPrincipal() {
        return route(GET("/auth/principal"), authWebHandler::getPrincipal);
    }
}
