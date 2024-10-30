package org.burgas.gatewayserver.handler;

import lombok.RequiredArgsConstructor;
import org.burgas.gatewayserver.dto.IdentityResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class RestTemplateHandler {

    private final RestTemplate restTemplate;

    public Mono<IdentityResponse> getIdentityByUsername(String username) {
        //noinspection BlockingMethodInNonBlockingContext
        return Mono.just(
                Objects.requireNonNull(
                        restTemplate.getForEntity(
                                URI.create("http://localhost:8888/identities/" + username),
                                IdentityResponse.class
                        )
                        .getBody()
                )
        );
    }
}
