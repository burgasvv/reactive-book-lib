package org.burgas.bookservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Genre implements Persistable<Long> {

    @Id
    private Long id;
    private String name;

    @Transient
    public Boolean isNew;

    @Override
    public boolean isNew() {
        return isNew || id == null;
    }
}
