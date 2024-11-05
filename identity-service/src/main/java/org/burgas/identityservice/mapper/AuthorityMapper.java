package org.burgas.identityservice.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.identityservice.dto.AuthorityRequest;
import org.burgas.identityservice.dto.AuthorityResponse;
import org.burgas.identityservice.entity.Authority;
import org.burgas.identityservice.repository.AuthorityRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class AuthorityMapper {

    private final AuthorityRepository authorityRepository;

    public Mono<Authority> toAuthority(Mono<AuthorityRequest> authorityRequestMono) {
        return authorityRequestMono
                .publishOn(Schedulers.boundedElastic())
                .handle(
                        (authorityRequest, authoritySynchronousSink) ->
                        {
                            try {
                                Long tempId = authorityRequest.getId() == null ? 0L : authorityRequest.getId();
                                authoritySynchronousSink.next(
                                        Authority.builder()
                                                .id(tempId)
                                                .name(authorityRequest.getName())
                                                .isNew(authorityRepository.findById(tempId).toFuture().get() == null)
                                                .build()
                                );
                            } catch (InterruptedException | ExecutionException e) {
                                authoritySynchronousSink.error(new RuntimeException(e));
                            }
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
