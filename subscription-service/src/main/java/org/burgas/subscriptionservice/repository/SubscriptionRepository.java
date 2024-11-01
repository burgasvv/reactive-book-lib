package org.burgas.subscriptionservice.repository;

import org.burgas.subscriptionservice.entity.Subscription;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface SubscriptionRepository extends R2dbcRepository<Subscription, Long> {

    @Query(
            value = """
                    select * from subscription where identity_id = :identityId
                    """
    )
    Flux<Subscription> findSubscriptionsByIdentityId(Long identityId);
}
