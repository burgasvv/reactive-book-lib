package org.burgas.bookservice.service;

import lombok.RequiredArgsConstructor;
import org.burgas.bookservice.dto.GenreRequest;
import org.burgas.bookservice.dto.GenreResponse;
import org.burgas.bookservice.dto.IdentityPrincipal;
import org.burgas.bookservice.handler.WebClientHandler;
import org.burgas.bookservice.mapper.GenreMapper;
import org.burgas.bookservice.repository.GenreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;

import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;
    private final WebClientHandler webClientHandler;

    public Flux<GenreResponse> findAll() {
        return genreRepository.findAll().cache(Duration.ofMinutes(60))
                .flatMap(genre -> genreMapper.toGenreResponse(Mono.just(genre)));
    }

    public Mono<GenreResponse> findById(String genreId) {
        return genreRepository.findById(Long.valueOf(genreId)).cache(Duration.ofMinutes(60))
                .flatMap(genre -> genreMapper.toGenreResponse(Mono.just(genre)));
    }

    @Transactional(
            isolation = SERIALIZABLE,
            propagation = REQUIRED,
            rollbackFor = Exception.class
    )
    public Mono<GenreResponse> createOrUpdate(Mono<GenreRequest> genreRequestMono, String authValue) {
        return Mono.zip(genreRequestMono, webClientHandler.getPrincipal(authValue))
                .flatMap(
                        objects -> {
                            GenreRequest genreRequest = objects.getT1();
                            IdentityPrincipal identityPrincipal = objects.getT2();
                            if (
                                    identityPrincipal.getIsAuthenticated() &&
                                    Objects.equals(identityPrincipal.getAuthorities().getFirst(), "ADMIN")
                            ) {
                                return genreMapper.toGenre(Mono.just(genreRequest))
                                        .flatMap(genreRepository::save).cache(Duration.ofMinutes(60))
                                        .flatMap(genre -> genreMapper.toGenreResponse(Mono.just(genre)));
                            } else
                                return Mono.error(
                                        new RuntimeException("Пользователь не авторизован или не имеет прав доступа")
                                );
                        }
                )
                .log("GENRE_SERVICE::createOrUpdate");
    }
}
