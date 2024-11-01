package org.burgas.bookservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;

import java.sql.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Author implements Persistable<Long> {

    @Id
    private Long id;
    private String firstname;
    private String lastname;
    private String patronymic;
    private String biography;

    @Column("birth_date")
    private Date birthdate;

    @Column("death_date")
    private Date deathdate;

    @Transient
    public Boolean isNew;

    @Override
    public boolean isNew() {
        return isNew;
    }
}
