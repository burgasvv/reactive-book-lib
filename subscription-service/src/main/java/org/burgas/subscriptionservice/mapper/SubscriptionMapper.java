package org.burgas.subscriptionservice.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.subscriptionservice.dto.SubscriptionRequest;
import org.burgas.subscriptionservice.dto.SubscriptionResponse;
import org.burgas.subscriptionservice.entity.Subscription;
import org.burgas.subscriptionservice.handler.WebClientHandler;
import org.burgas.subscriptionservice.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

import static java.time.format.DateTimeFormatter.ofPattern;

@Service
@RequiredArgsConstructor
public class SubscriptionMapper {

    private final SubscriptionRepository subscriptionRepository;
    private final WebClientHandler webClientHandler;

    public Subscription toSubscriptionCreate(SubscriptionRequest subscriptionRequest) {
        return Subscription.builder()
                .id(subscriptionRequest.getId())
                .title(subscriptionRequest.getTitle())
                .identityId(subscriptionRequest.getIdentityId())
                .created(LocalDateTime.now())
                .active(false)
                .paid(false)
                .isNew(true)
                .build();
    }

    public Subscription toSubscriptionUpdate(SubscriptionRequest subscriptionRequest) {
        try {

            Subscription subscription = subscriptionRepository
                    .findById(subscriptionRequest.getId()).toFuture().get();
            return Subscription.builder()
                    .id(subscriptionRequest.getId())
                    .title(subscriptionRequest.getTitle())
                    .identityId(subscription.getIdentityId())
                    .created(subscription.getCreated())
                    .updated(subscription.getUpdated())
                    .active(subscription.getActive())
                    .paid(subscription.getPaid())
                    .isNew(false)
                    .build();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public SubscriptionResponse toSubscriptionResponse(Subscription subscription, String authorizeValue) {
        try {
            LocalDateTime created = subscription.getCreated();
            LocalDateTime updated = subscription.getUpdated();
            LocalDateTime ended = subscription.getEnded();
            return SubscriptionResponse.builder()
                    .id(subscription.getId())
                    .title(subscription.getTitle())
                    .active(subscription.getActive())
                    .paid(subscription.getPaid())
                    .created(created != null ? created.format(ofPattern("yyyy-MM-dd HH:mm:ss")) : null)
                    .updated(updated != null ? updated.format(ofPattern("yyyy-MM-dd HH:mm:ss")) : null)
                    .ended(ended != null ? ended.format(ofPattern("yyyy-MM-dd HH:mm:ss")) : null)
                    .identityResponse(
                            webClientHandler.getIdentityById(subscription.getIdentityId(), authorizeValue).toFuture().get()
                    )
                    .bookResponses(
                            webClientHandler.getBooksBySubscriptionId(subscription.getId(), authorizeValue)
                                    .collectList().toFuture().get()
                    )
                    .build();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
