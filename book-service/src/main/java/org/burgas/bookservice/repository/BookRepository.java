package org.burgas.bookservice.repository;

import org.burgas.bookservice.entity.Book;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface BookRepository extends ReactiveCrudRepository<Book, Long> {

    @Query(
            value = """
                    select * from book join subscription_book sb on book.id = sb.book_id
                    where sb.subscription_id = :subscription_id
                    """
    )
    Flux<Book> findBooksBySubscriptionId(Long subscriptionId);

    Flux<Book> findBooksByGenreId(Long genreId);

    Flux<Book> findBooksByAuthorId(Long authorId);
}
