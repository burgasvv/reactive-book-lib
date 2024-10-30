package org.burgas.identityservice.service;

import lombok.RequiredArgsConstructor;
import org.burgas.identityservice.dto.AuthorityRequest;
import org.burgas.identityservice.dto.AuthorityResponse;
import org.burgas.identityservice.mapper.AuthorityMapper;
import org.burgas.identityservice.repository.AuthorityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;

import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorityService {

    private final AuthorityRepository authorityRepository;
    private final AuthorityMapper authorityMapper;

    public Flux<AuthorityResponse> findAll() {
        return authorityRepository.findAll()
                .map(authorityMapper::toAuthorityResponse);

    }

    public Mono<AuthorityResponse> findById(String authorityId) {
        return authorityRepository.findById(Long.valueOf(authorityId))
                .map(authorityMapper::toAuthorityResponse);
    }

    @Transactional(
            isolation = SERIALIZABLE,
            propagation = REQUIRED,
            rollbackFor = Exception.class
    )
    public Mono<AuthorityResponse> createOrUpdate(Mono<AuthorityRequest> authorityRequestMono) {
        try {
            //noinspection BlockingMethodInNonBlockingContext
            return Mono.just(
                    authorityMapper.toAuthorityResponse(
                            authorityRepository.save(
                                    authorityMapper.toAuthority(
                                            authorityRequestMono.toFuture().get()
                                    )
                            )
                                    .toFuture().get()
                    )
            );

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
