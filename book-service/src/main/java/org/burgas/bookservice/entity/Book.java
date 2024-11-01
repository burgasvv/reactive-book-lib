package org.burgas.bookservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book implements Persistable<Long> {

    @Id
    private Long id;
    private String title;
    private String description;

    @Column("author_id")
    private Long authorId;

    @Column("genre_id")
    private Long genreId;

    @Transient
    public Boolean isNew;

    @Override
    public boolean isNew() {
        return isNew;
    }
}
