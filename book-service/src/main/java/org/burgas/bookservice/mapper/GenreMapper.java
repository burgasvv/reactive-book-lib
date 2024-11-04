package org.burgas.bookservice.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.bookservice.dto.GenreRequest;
import org.burgas.bookservice.dto.GenreResponse;
import org.burgas.bookservice.entity.Genre;
import org.burgas.bookservice.repository.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class GenreMapper {

    private final GenreRepository genreRepository;

    public Genre toGenre(GenreRequest genreRequest) {
        try {
            return Genre.builder()
                    .id(genreRequest.getId())
                    .name(genreRequest.getName())
                    .isNew(genreRepository.findById(genreRequest.getId()).toFuture().get() == null)
                    .build();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public GenreResponse toGenreResponse(Genre genre) {
        return GenreResponse.builder()
                .id(genre.getId())
                .name(genre.getName())
                .build();
    }
}
