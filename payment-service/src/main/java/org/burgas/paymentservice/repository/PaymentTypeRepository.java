package org.burgas.paymentservice.repository;

import org.burgas.paymentservice.entity.PaymentType;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentTypeRepository extends ReactiveCrudRepository<PaymentType, Long> {
}
