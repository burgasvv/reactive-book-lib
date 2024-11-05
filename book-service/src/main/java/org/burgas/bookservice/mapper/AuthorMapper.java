package org.burgas.bookservice.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.bookservice.dto.AuthorRequest;
import org.burgas.bookservice.dto.AuthorResponse;
import org.burgas.bookservice.entity.Author;
import org.burgas.bookservice.repository.AuthorRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class AuthorMapper {

    private final AuthorRepository authorRepository;

    public Mono<Author> toAuthor(Mono<AuthorRequest> authorRequestMono) {
        return authorRequestMono
                .publishOn(Schedulers.boundedElastic())
                .handle(
                        (authorRequest, authorSynchronousSink) ->
                        {
                            try {
                                Long tempId = authorRequest.getId() == null ? 0L : authorRequest.getId();
                                authorSynchronousSink.next(
                                        Author.builder()
                                                .id(authorRequest.getId())
                                                .firstname(authorRequest.getFirstname())
                                                .lastname(authorRequest.getLastname())
                                                .patronymic(authorRequest.getPatronymic())
                                                .isNew(authorRepository.findById(tempId).toFuture().get() == null)
                                                .build()
                                );
                            } catch (InterruptedException | ExecutionException e) {
                                authorSynchronousSink.error(new RuntimeException(e));
                            }
                        }
                );
    }

    public Mono<AuthorResponse> toAuthorResponse(Mono<Author> authorMono) {
        return authorMono
                .map(
                        author -> AuthorResponse.builder()
                                .id(author.getId())
                                .firstname(author.getFirstname())
                                .lastname(author.getLastname())
                                .patronymic(author.getPatronymic())
                                .build()
                );
    }
}
