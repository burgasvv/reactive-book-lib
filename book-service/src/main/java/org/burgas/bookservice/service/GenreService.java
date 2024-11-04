package org.burgas.bookservice.service;

import lombok.RequiredArgsConstructor;
import org.burgas.bookservice.dto.GenreRequest;
import org.burgas.bookservice.dto.GenreResponse;
import org.burgas.bookservice.entity.Genre;
import org.burgas.bookservice.mapper.GenreMapper;
import org.burgas.bookservice.repository.GenreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    public Flux<GenreResponse> findAll() {
        return genreRepository.findAll().map(genreMapper::toGenreResponse);
    }

    public Mono<GenreResponse> findById(String genreId) {
        return genreRepository.findById(Long.valueOf(genreId)).map(genreMapper::toGenreResponse);
    }

    @Transactional(
            isolation = SERIALIZABLE,
            propagation = REQUIRED,
            rollbackFor = Exception.class
    )
    public Mono<GenreResponse> createOrUpdate(Mono<GenreRequest> genreRequestMono) {
        return genreRequestMono.flatMap(
                genreRequest -> {
                    Genre genre = genreMapper.toGenre(genreRequest);
                    return genreRepository.save(genre).map(genreMapper::toGenreResponse);
                }
        );
    }
}
