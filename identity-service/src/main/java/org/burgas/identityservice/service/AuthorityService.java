package org.burgas.identityservice.service;

import lombok.RequiredArgsConstructor;
import org.burgas.identityservice.dto.AuthorityRequest;
import org.burgas.identityservice.dto.AuthorityResponse;
import org.burgas.identityservice.handler.WebClientHandler;
import org.burgas.identityservice.mapper.AuthorityMapper;
import org.burgas.identityservice.repository.AuthorityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static org.springframework.transaction.annotation.Isolation.REPEATABLE_READ;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorityService {

    private final AuthorityRepository authorityRepository;
    private final AuthorityMapper authorityMapper;
    private final WebClientHandler webClientHandler;

    public Flux<AuthorityResponse> findAll() {
        return authorityRepository.findAll()
                .flatMap(authority -> authorityMapper.toAuthorityResponse(Mono.just(authority)));
    }

    public Mono<AuthorityResponse> findById(String authorityId) {
        return authorityRepository.findById(Long.valueOf(authorityId))
                .flatMap(authority -> authorityMapper.toAuthorityResponse(Mono.just(authority)));
    }

    @Transactional(
            isolation = REPEATABLE_READ,
            propagation = REQUIRED,
            rollbackFor = Exception.class
    )
    public Mono<AuthorityResponse> createOrUpdate(Mono<AuthorityRequest> authorityRequestMono, String authValue) {
        return webClientHandler.getPrincipal(authValue)
                .flatMap(
                        identityPrincipal -> {
                            if (
                                    identityPrincipal.getIsAuthenticated() &&
                                    Objects.equals(identityPrincipal.getAuthorities().get(0), "ADMIN")
                            ) {
                                return authorityMapper.toAuthority(authorityRequestMono)
                                        .flatMap(authorityRepository::save)
                                        .flatMap(authority -> authorityMapper.toAuthorityResponse(Mono.just(authority)));
                            } else
                                return Mono.error(
                                        new RuntimeException("Пользователь не авторизован или не имеет прав доступа")
                                );
                        }
                );
    }
}
