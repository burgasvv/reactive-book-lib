package org.burgas.bookservice.service;

import lombok.RequiredArgsConstructor;
import org.burgas.bookservice.dto.AuthorRequest;
import org.burgas.bookservice.dto.AuthorResponse;
import org.burgas.bookservice.handler.WebClientHandler;
import org.burgas.bookservice.mapper.AuthorMapper;
import org.burgas.bookservice.repository.AuthorRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;

import static org.springframework.transaction.annotation.Isolation.REPEATABLE_READ;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final WebClientHandler webClientHandler;

    @Cacheable("authors")
    public Flux<AuthorResponse> findAll() {
        return authorRepository.findAll().cache(Duration.ofMinutes(60))
                .flatMap(author -> authorMapper.toAuthorResponse(Mono.just(author)));
    }

    @Cacheable("author")
    public Mono<AuthorResponse> findById(String authorId) {
        return authorRepository.findById(Long.valueOf(authorId)).cache(Duration.ofMinutes(60))
                .flatMap(author -> authorMapper.toAuthorResponse(Mono.just(author)));
    }

    @CachePut("author")
    @Transactional(
            isolation = REPEATABLE_READ,
            propagation = REQUIRED,
            rollbackFor = Exception.class
    )
    public Mono<AuthorResponse> createOrUpdate(Mono<AuthorRequest> authorRequestMono, String authValue) {
        return authorRequestMono.flatMap(
                authorRequest -> webClientHandler.getPrincipal(authValue)
                        .flatMap(
                                identityPrincipal -> {
                                    if (
                                            identityPrincipal.getIsAuthenticated() &&
                                            Objects.equals(identityPrincipal.getAuthorities().get(0), "ADMIN")
                                    ) {
                                        return authorMapper.toAuthor(Mono.just(authorRequest))
                                                .flatMap(authorRepository::save).cache(Duration.ofMinutes(60))
                                                .flatMap(author -> authorMapper.toAuthorResponse(Mono.just(author)));
                                    } else
                                        return Mono.error(
                                                new RuntimeException("Пользователь не авторизован или не имеет прав доступа")
                                        );
                                }
                        )
        )
                .log("AUTHOR_SERVICE::createOrUpdate");
    }
}
