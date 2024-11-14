package org.burgas.identityservice.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.identityservice.dto.AuthorityRequest;
import org.burgas.identityservice.dto.AuthorityResponse;
import org.burgas.identityservice.entity.Authority;
import org.burgas.identityservice.repository.AuthorityRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthorityMapper {

    private final AuthorityRepository authorityRepository;

    public Mono<Authority> toAuthority(Mono<AuthorityRequest> authorityRequestMono) {
        return authorityRequestMono.flatMap(
                authorityRequest -> {
                    Long authorityId = authorityRequest.getId() == null ? 0L : authorityRequest.getId();
                    return authorityRepository.findById(authorityId)
                            .mapNotNull(authority -> authority)
                            .flatMap(
                                    _ -> Mono.just(
                                            Authority.builder()
                                                    .id(authorityRequest.getId())
                                                    .name(authorityRequest.getName())
                                                    .isNew(false)
                                                    .build()
                                    )
                            )
                            .switchIfEmpty(
                                    Mono.just(
                                            Authority.builder()
                                                    .id(authorityRequest.getId())
                                                    .name(authorityRequest.getName())
                                                    .isNew(true)
                                                    .build()
                                    )
                            );
                }
        );
    }

    public Mono<AuthorityResponse> toAuthorityResponse(Mono<Authority> authorityMono) {
        return authorityMono
                .map(
                        authority -> AuthorityResponse.builder()
                                .id(authority.getId())
                                .name(authority.getName())
                                .build()
                );
    }
}
