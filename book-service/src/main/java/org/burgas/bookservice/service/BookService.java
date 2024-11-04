package org.burgas.bookservice.service;

import lombok.RequiredArgsConstructor;
import org.burgas.bookservice.dto.BookRequest;
import org.burgas.bookservice.dto.BookResponse;
import org.burgas.bookservice.entity.Book;
import org.burgas.bookservice.mapper.BookMapper;
import org.burgas.bookservice.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public Flux<BookResponse> findAll() {
        return bookRepository.findAll().map(bookMapper::toBookResponse);
    }

    public Mono<BookResponse> findById(String bookId) {
        return bookRepository.findById(Long.valueOf(bookId)).map(bookMapper::toBookResponse);
    }

    public Flux<BookResponse> findBySubscriptionId(String subscriptionId) {
        return bookRepository.findBooksBySubscriptionId(Long.valueOf(subscriptionId)).map(bookMapper::toBookResponse);
    }

    public Flux<BookResponse> findByGenreId(String genreId) {
        return bookRepository.findBooksByGenreId(Long.valueOf(genreId)).map(bookMapper::toBookResponse);
    }

    public Flux<BookResponse> findByAuthorId(String authorId) {
        return bookRepository.findBooksByAuthorId(Long.valueOf(authorId)).map(bookMapper::toBookResponse);
    }

    @Transactional(
            isolation = SERIALIZABLE,
            propagation = REQUIRED,
            rollbackFor = Exception.class
    )
    public Mono<BookResponse> createOrUpdate(Mono<BookRequest> bookRequestMono) {
        return bookRequestMono.flatMap(
                bookRequest -> {
                    Book book = bookMapper.toBook(bookRequest);
                    return bookRepository.save(book).map(bookMapper::toBookResponse);
                }
        );
    }
}
