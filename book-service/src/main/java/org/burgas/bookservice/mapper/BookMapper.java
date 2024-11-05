package org.burgas.bookservice.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.bookservice.dto.BookRequest;
import org.burgas.bookservice.dto.BookResponse;
import org.burgas.bookservice.entity.Book;
import org.burgas.bookservice.repository.AuthorRepository;
import org.burgas.bookservice.repository.BookRepository;
import org.burgas.bookservice.repository.GenreRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class BookMapper {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;
    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final BookRepository bookRepository;

    public Mono<Book> toBook(Mono<BookRequest> bookRequestMono) {
        return bookRequestMono
                .publishOn(Schedulers.boundedElastic())
                .handle(
                        (bookRequest, bookSynchronousSink) ->
                        {
                            try {
                                Long tempId = bookRequest.getId() == null ? 0L : bookRequest.getId();
                                bookSynchronousSink.next(
                                        Book.builder()
                                                .id(tempId)
                                                .title(bookRequest.getTitle())
                                                .pages(bookRequest.getPages())
                                                .description(bookRequest.getDescription())
                                                .authorId(bookRequest.getAuthorId())
                                                .genreId(bookRequest.getGenreId())
                                                .isNew(bookRepository.findById(tempId).toFuture().get() == null)
                                                .build()
                                );
                            } catch (InterruptedException | ExecutionException e) {
                                bookSynchronousSink.error(new RuntimeException(e));
                            }
                        }
                );
    }

    public Mono<BookResponse> toBookResponse(Mono<Book> bookMono) {
        return bookMono
                .publishOn(Schedulers.boundedElastic())
                .handle(
                        (book, bookResponseSynchronousSink) ->
                        {
                            try {
                                bookResponseSynchronousSink.next(
                                        BookResponse.builder()
                                                .id(book.getId())
                                                .title(book.getTitle())
                                                .pages(book.getPages())
                                                .description(book.getDescription())
                                                .authorResponse(
                                                        authorMapper.toAuthorResponse(authorRepository.findById(book.getAuthorId()))
                                                                .toFuture().get()
                                                )
                                                .genreResponse(
                                                        genreMapper.toGenreResponse(genreRepository.findById(book.getGenreId()))
                                                                .toFuture().get()
                                                )
                                                .build()
                                );
                            } catch (InterruptedException | ExecutionException e) {
                                bookResponseSynchronousSink.error(new RuntimeException(e));
                            }
                        }
                );
    }
}
