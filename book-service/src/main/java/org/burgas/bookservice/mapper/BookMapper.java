package org.burgas.bookservice.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.bookservice.dto.BookRequest;
import org.burgas.bookservice.dto.BookResponse;
import org.burgas.bookservice.entity.Book;
import org.burgas.bookservice.repository.AuthorRepository;
import org.burgas.bookservice.repository.BookRepository;
import org.burgas.bookservice.repository.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class BookMapper {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;
    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final BookRepository bookRepository;

    public Book toBook(BookRequest bookRequest) {
        try {
            return Book.builder()
                    .id(bookRequest.getId())
                    .title(bookRequest.getTitle())
                    .pages(bookRequest.getPages())
                    .description(bookRequest.getDescription())
                    .authorId(bookRequest.getAuthorId())
                    .genreId(bookRequest.getGenreId())
                    .isNew(bookRepository.findById(bookRequest.getId()).toFuture().get() == null)
                    .build();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public BookResponse toBookResponse(Book book) {
        try {
            return BookResponse.builder()
                    .id(book.getId())
                    .title(book.getTitle())
                    .pages(book.getPages())
                    .description(book.getDescription())
                    .authorResponse(
                            authorMapper.toAuthorResponse(authorRepository.findById(book.getAuthorId()).toFuture().get())
                    )
                    .genreResponse(
                            genreMapper.toGenreResponse(genreRepository.findById(book.getGenreId()).toFuture().get())
                    )
                    .build();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
