package org.burgas.gatewayserver.controller;

import lombok.RequiredArgsConstructor;
import org.burgas.gatewayserver.dto.IdentityPrincipal;
import org.burgas.gatewayserver.dto.IdentityResponse;
import org.burgas.gatewayserver.mapper.IdentityMapper;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final IdentityMapper identityMapper;

    @GetMapping("/principal")
    public Mono<IdentityPrincipal> getPrincipal(Authentication authentication) {
        if (authentication != null) {
            IdentityResponse principal = (IdentityResponse) authentication.getPrincipal();
            return Mono.just(
                    identityMapper.toIdentityPrincipal(principal, true)
            );

        } else
            return Mono.just(IdentityPrincipal.builder().isAuthenticated(false).build());
    }
}
