package org.burgas.identityservice.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.identityservice.dto.IdentityRequest;
import org.burgas.identityservice.dto.IdentityResponse;
import org.burgas.identityservice.entity.Identity;
import org.burgas.identityservice.repository.IdentityRepository;
import org.burgas.identityservice.service.AuthorityService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class IdentityMapper {

    private final AuthorityService authorityService;
    private final IdentityRepository identityRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<Identity> toIdentityCreate(Mono<IdentityRequest> identityRequestMono) {
        return identityRequestMono
                .map(
                        identityRequest -> Identity.builder()
                                .id(identityRequest.getId())
                                .username(identityRequest.getUsername())
                                .password(passwordEncoder.encode(identityRequest.getPassword()))
                                .email(identityRequest.getEmail())
                                .authorityId(1L)
                                .enabled(true)
                                .isNew(true)
                                .build()
                );
    }

    public Mono<Identity> toIdentityUpdate(Mono<IdentityRequest> identityRequestMono) {
        return identityRequestMono.flatMap(
                identityRequest -> {
                    Long identityId = identityRequest.getId() == null ? 0L : identityRequest.getId();
                    return identityRepository.findById(identityId)
                            .flatMap(
                                    identity -> Mono.just(
                                            Identity.builder()
                                                    .id(identityRequest.getId())
                                                    .username(identityRequest.getUsername())
                                                    .password(identity.getPassword())
                                                    .email(identityRequest.getEmail())
                                                    .authorityId(identity.getAuthorityId())
                                                    .enabled(identity.getEnabled())
                                                    .isNew(false)
                                                    .build()
                                    )
                            );
                }
        );
    }

    public Mono<IdentityResponse> toIdentityResponse(Mono<Identity> identityMono) {
        return identityMono.flatMap(
                identity -> authorityService.findById(String.valueOf(identity.getAuthorityId()))
                        .flatMap(
                                authorityResponse -> Mono.just(
                                        IdentityResponse.builder()
                                                .id(identity.getId())
                                                .username(identity.getUsername())
                                                .password(identity.getPassword())
                                                .email(identity.getEmail())
                                                .enabled(identity.getEnabled())
                                                .authorityResponse(authorityResponse)
                                                .build()
                                )
                        )
        );
    }
}
