package org.burgas.gatewayserver.service;

import lombok.RequiredArgsConstructor;
import org.burgas.gatewayserver.handler.RestTemplateHandler;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements ReactiveUserDetailsService {

    private final RestTemplateHandler restTemplateHandler;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        try {
            //noinspection BlockingMethodInNonBlockingContext
            return Mono.just(restTemplateHandler.getIdentityByUsername(username).toFuture().get());

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
