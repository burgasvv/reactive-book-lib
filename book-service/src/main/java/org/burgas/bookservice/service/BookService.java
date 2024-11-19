package org.burgas.bookservice.service;

import lombok.RequiredArgsConstructor;
import org.burgas.bookservice.dto.BookRequest;
import org.burgas.bookservice.dto.BookResponse;
import org.burgas.bookservice.handler.WebClientHandler;
import org.burgas.bookservice.mapper.BookMapper;
import org.burgas.bookservice.repository.BookRepository;
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
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final WebClientHandler webClientHandler;

    public Flux<BookResponse> findAll() {
        return bookRepository.findAll().cache(Duration.ofMinutes(60))
                .flatMap(book -> bookMapper.toBookResponse(Mono.just(book)));
    }

    public Mono<BookResponse> findById(String bookId) {
        return bookRepository.findById(Long.valueOf(bookId)).cache(Duration.ofMinutes(60))
                .flatMap(book -> bookMapper.toBookResponse(Mono.just(book)));
    }

    public Flux<BookResponse> findBySubscriptionId(String subscriptionId) {
        return bookRepository.findBooksBySubscriptionId(Long.valueOf(subscriptionId))
                .cache(Duration.ofMinutes(60))
                .flatMap(book -> bookMapper.toBookResponse(Mono.just(book)));
    }

    public Flux<BookResponse> findByGenreId(String genreId) {
        return bookRepository.findBooksByGenreId(Long.valueOf(genreId))
                .cache(Duration.ofMinutes(60))
                .flatMap(book -> bookMapper.toBookResponse(Mono.just(book)));
    }

    public Flux<BookResponse> findByAuthorId(String authorId) {
        return bookRepository.findBooksByAuthorId(Long.valueOf(authorId))
                .cache(Duration.ofSeconds(60))
                .flatMap(book -> bookMapper.toBookResponse(Mono.just(book)))
                .log("BOOK_SERVICE::findByAuthorId");
    }

    @Transactional(
            isolation = SERIALIZABLE, propagation = REQUIRED, rollbackFor = Exception.class
    )
    public Mono<BookResponse> createOrUpdate(Mono<BookRequest> bookRequestMono, String authValue) {
        return bookRequestMono.flatMap(
                bookRequest -> webClientHandler.getPrincipal(authValue)
                        .flatMap(
                                identityPrincipal -> {
                                    if (
                                            identityPrincipal.getIsAuthenticated() &&
                                            Objects.equals(identityPrincipal.getAuthorities().getFirst(), "ADMIN")
                                    ) {
                                        return bookMapper.toBook(Mono.just(bookRequest))
                                                .flatMap(bookRepository::save).cache(Duration.ofMinutes(60))
                                                .flatMap(book -> bookMapper.toBookResponse(Mono.just(book)));

                                    } else
                                        return Mono.error(
                                                new RuntimeException("Пользователь не авторизован и не имеет прав доступа")
                                        );
                                }
                        )
        )
                .log("BOOK_SERVICE::createOrUpdate");
    }
}
