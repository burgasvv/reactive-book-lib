package org.burgas.bookservice.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.bookservice.dto.GenreRequest;
import org.burgas.bookservice.dto.GenreResponse;
import org.burgas.bookservice.entity.Genre;
import org.burgas.bookservice.repository.GenreRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class GenreMapper {

    private final GenreRepository genreRepository;

    public Mono<Genre> toGenre(Mono<GenreRequest> genreRequestMono) {
        return genreRequestMono
                .publishOn(Schedulers.boundedElastic())
                .handle(
                        (genreRequest, genreSynchronousSink) ->
                        {
                            try {
                                Long genreId = genreRequest.getId() == null ? 0L : genreRequest.getId();
                                genreSynchronousSink.next(
                                        Genre.builder()
                                                .id(genreRequest.getId())
                                                .name(genreRequest.getName())
                                                .isNew(genreRepository.findById(genreId).toFuture().get() == null)
                                                .build()
                                );
                            } catch (InterruptedException | ExecutionException e) {
                                genreSynchronousSink.error(new RuntimeException(e));
                            }
                        }
                );
    }

    public Mono<GenreResponse> toGenreResponse(Mono<Genre> genreMono) {
        return genreMono
                .map(
                        genre -> GenreResponse.builder()
                                .id(genre.getId())
                                .name(genre.getName())
                                .build()
                );
    }
}
