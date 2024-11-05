package org.burgas.paymentservice.entity;

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
public class Payment implements Persistable<Long> {

    @Id
    private Long id;

    @Column("payment_type_id")
    private Long paymentTypeId;

    @Column("subscription_id")
    private Long subscriptionId;

    @Transient
    private Boolean isNew;

    @Override
    public boolean isNew() {
        return isNew;
    }
}
