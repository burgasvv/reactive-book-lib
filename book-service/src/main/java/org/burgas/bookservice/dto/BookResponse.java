package org.burgas.bookservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {

    private Long id;
    private String title;
    private Long pages;
    private String description;
    private AuthorResponse authorResponse;
    private GenreResponse genreResponse;
}
