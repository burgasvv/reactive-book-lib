package org.burgas.bookservice.repository;

import org.burgas.bookservice.entity.Genre;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends ReactiveCrudRepository<Genre, Long> {
}
