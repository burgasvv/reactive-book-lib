package org.burgas.bookservice.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.bookservice.dto.AuthorRequest;
import org.burgas.bookservice.dto.AuthorResponse;
import org.burgas.bookservice.entity.Author;
import org.burgas.bookservice.repository.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class AuthorMapper {

    private final AuthorRepository authorRepository;

    public Author toAuthor(AuthorRequest authorRequest) {
        try {
            return Author.builder()
                    .id(authorRequest.getId())
                    .firstname(authorRequest.getFirstname())
                    .lastname(authorRequest.getLastname())
                    .patronymic(authorRequest.getPatronymic())
                    .isNew(authorRepository.findById(authorRequest.getId()).toFuture().get() == null)
                    .build();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public AuthorResponse toAuthorResponse(Author author) {
        return AuthorResponse.builder()
                .id(author.getId())
                .firstname(author.getFirstname())
                .lastname(author.getLastname())
                .patronymic(author.getPatronymic())
                .build();
    }
}
