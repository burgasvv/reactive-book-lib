package org.burgas.bookservice.service;

import lombok.RequiredArgsConstructor;
import org.burgas.bookservice.dto.AuthorRequest;
import org.burgas.bookservice.dto.AuthorResponse;
import org.burgas.bookservice.entity.Author;
import org.burgas.bookservice.mapper.AuthorMapper;
import org.burgas.bookservice.repository.AuthorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    public Flux<AuthorResponse> findAll() {
        return authorRepository.findAll().map(authorMapper::toAuthorResponse);
    }

    public Mono<AuthorResponse> findById(String authorId) {
        return authorRepository.findById(Long.valueOf(authorId)).map(authorMapper::toAuthorResponse);
    }

    public Mono<AuthorResponse> createOrUpdate(Mono<AuthorRequest> authorRequestMono) {
        return authorRequestMono.flatMap(
                authorRequest -> {
                    Author author = authorMapper.toAuthor(authorRequest);
                    return authorRepository.save(author).map(authorMapper::toAuthorResponse);
                }
        );
    }
}
