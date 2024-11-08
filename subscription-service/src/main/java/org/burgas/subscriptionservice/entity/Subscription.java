package org.burgas.subscriptionservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription implements Persistable<Long> {

    @Id
    private Long id;
    private String title;

    @Column("identity_id")
    private Long identityId;
    private Boolean active;
    private LocalDateTime created;
    private LocalDateTime updated;
    private LocalDateTime ended;
    private Boolean paid;

    @Transient
    private Boolean isNew;

    @Override
    public boolean isNew() {
        return isNew || id == null;
    }
}
