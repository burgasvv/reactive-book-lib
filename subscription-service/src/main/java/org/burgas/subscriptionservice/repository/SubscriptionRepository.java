package org.burgas.subscriptionservice.repository;

import org.burgas.subscriptionservice.entity.Subscription;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SubscriptionRepository extends ReactiveCrudRepository<Subscription, Long> {

    @Query(
            value = """
                    select * from subscription where identity_id = :identityId
                    """
    )
    Flux<Subscription> findSubscriptionsByIdentityId(Long identityId);

    @Query(
            value = """
                    insert into subscription_book(subscription_id, book_id) VALUES (:subscriptionId, :bookId)
                    """
    )
    Mono<Void> addBookToSubscription(Long subscriptionId, Long bookId);
}
