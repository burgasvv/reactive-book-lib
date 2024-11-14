package org.burgas.gatewayserver.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.gatewayserver.dto.IdentityPrincipal;
import org.burgas.gatewayserver.dto.IdentityResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IdentityMapper {

    public Mono<IdentityPrincipal> toIdentityPrincipal(
            Mono<IdentityResponse> identityResponseMono, Boolean isAuthenticated
    ) {
        return identityResponseMono.flatMap(
                identityResponse -> Mono.just(
                        IdentityPrincipal.builder()
                                .id(identityResponse.getId())
                                .username(identityResponse.getUsername())
                                .password(identityResponse.getPassword())
                                .authorities(List.of(identityResponse.getAuthorityResponse().getAuthority()))
                                .enabled(identityResponse.getEnabled())
                                .isAuthenticated(isAuthenticated)
                                .build()
                )
        );
    }
}
