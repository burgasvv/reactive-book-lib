package org.burgas.gatewayserver.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProxyConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(
                        "identities",
                        predicateSpec -> predicateSpec
                                .path("/identities/**", "/authorities/**")
                                .uri("lb://identity-service")
                )
                .route(
                        "subscriptions",
                        predicateSpec -> predicateSpec
                                .path("/subscriptions/**")
                                .uri("lb://subscription-service")
                )
                .build();
    }
}
