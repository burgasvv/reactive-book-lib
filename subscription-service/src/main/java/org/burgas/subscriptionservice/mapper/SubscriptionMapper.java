package org.burgas.subscriptionservice.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.subscriptionservice.dto.SubscriptionRequest;
import org.burgas.subscriptionservice.dto.SubscriptionResponse;
import org.burgas.subscriptionservice.entity.Subscription;
import org.burgas.subscriptionservice.handler.WebClientHandler;
import org.burgas.subscriptionservice.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

import static java.time.format.DateTimeFormatter.ofPattern;

@Service
@RequiredArgsConstructor
public class SubscriptionMapper {

    private final SubscriptionRepository subscriptionRepository;
    private final WebClientHandler webClientHandler;

    public Mono<Subscription> toSubscriptionCreate(Mono<SubscriptionRequest> subscriptionRequestMono) {
        return subscriptionRequestMono
                .map(
                        subscriptionRequest ->
                                Subscription.builder()
                                    .id(subscriptionRequest.getId() == null ? 0L : subscriptionRequest.getIdentityId())
                                    .title(subscriptionRequest.getTitle())
                                    .identityId(subscriptionRequest.getIdentityId())
                                    .created(LocalDateTime.now())
                                    .active(false)
                                    .paid(false)
                                    .isNew(true)
                                    .build()
                );
    }

    public Mono<Subscription> toSubscriptionUpdate(Mono<SubscriptionRequest> subscriptionRequestMono) {
        return subscriptionRequestMono
                .publishOn(Schedulers.boundedElastic())
                .handle(
                        (subscriptionRequest, subscriptionSynchronousSink) ->
                        {
                            try {
                                Subscription subscription = subscriptionRepository
                                        .findById(subscriptionRequest.getId()).toFuture().get();
                                subscriptionSynchronousSink.next(
                                        Subscription.builder()
                                                .id(subscriptionRequest.getId())
                                                .title(subscriptionRequest.getTitle())
                                                .identityId(subscription.getIdentityId())
                                                .created(subscription.getCreated())
                                                .updated(LocalDateTime.now())
                                                .active(subscription.getActive())
                                                .paid(true)
                                                .isNew(false)
                                                .build()
                                );

                            } catch (InterruptedException | ExecutionException e) {
                                subscriptionSynchronousSink.error(new RuntimeException(e));
                            }
                        }
                );
    }

    public Mono<SubscriptionResponse> toSubscriptionResponse(
            Mono<Subscription> subscriptionMono, String authorizeValue
    ) {
        return subscriptionMono
                .subscribeOn(Schedulers.boundedElastic())
                .handle(
                        (subscription, subscriptionResponseSynchronousSink) -> {
                            LocalDateTime created = subscription.getCreated();
                            LocalDateTime updated = subscription.getUpdated();
                            LocalDateTime ended = subscription.getEnded();
                            try {
                                subscriptionResponseSynchronousSink.next(
                                        SubscriptionResponse.builder()
                                                .id(subscription.getId())
                                                .title(subscription.getTitle())
                                                .active(subscription.getActive())
                                                .paid(subscription.getPaid())
                                                .created(created != null ? created.format(ofPattern("yyyy-MM-dd HH:mm:ss")) : null)
                                                .updated(updated != null ? updated.format(ofPattern("yyyy-MM-dd HH:mm:ss")) : null)
                                                .ended(ended != null ? ended.format(ofPattern("yyyy-MM-dd HH:mm:ss")) : null)
                                                .identityResponse(
                                                        webClientHandler.getIdentityById(subscription.getIdentityId(), authorizeValue)
                                                                .toFuture().get()
                                                )
                                                .bookResponses(
                                                        webClientHandler.getBooksBySubscriptionId(subscription.getId(), authorizeValue)
                                                                .collectList().toFuture().get()
                                                )
                                                .build()
                                );

                            } catch (InterruptedException | ExecutionException e) {
                                subscriptionResponseSynchronousSink.error(new RuntimeException(e));
                            }
                        }
                );
    }
}
