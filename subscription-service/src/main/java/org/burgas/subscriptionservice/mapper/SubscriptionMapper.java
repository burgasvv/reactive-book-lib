package org.burgas.subscriptionservice.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.subscriptionservice.dto.PaymentRequest;
import org.burgas.subscriptionservice.dto.SubscriptionRequest;
import org.burgas.subscriptionservice.dto.SubscriptionResponse;
import org.burgas.subscriptionservice.entity.Subscription;
import org.burgas.subscriptionservice.handler.WebClientHandler;
import org.burgas.subscriptionservice.repository.SubscriptionRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.ofPattern;

@Component
@RequiredArgsConstructor
public class SubscriptionMapper {

    private final SubscriptionRepository subscriptionRepository;
    private final WebClientHandler webClientHandler;

    public Mono<Subscription> toSubscriptionCreate(Mono<SubscriptionRequest> subscriptionRequestMono) {
        return subscriptionRequestMono
                .map(
                        subscriptionRequest ->
                                Subscription.builder()
                                        .id(subscriptionRequest.getId())
                                        .title(subscriptionRequest.getTitle())
                                        .identityId(subscriptionRequest.getIdentityId())
                                        .created(LocalDateTime.now())
                                        .active(false)
                                        .paid(false)
                                        .isNew(true)
                                        .build()
                ).log("SUBSCRIPTION-MAPPER toSubscriptionCreate");
    }

    public Mono<Subscription> toSubscriptionUpdate(Mono<PaymentRequest> paymentRequestMono) {
        return paymentRequestMono
                .flatMap(
                        paymentRequest -> subscriptionRepository.findById(paymentRequest.getSubscriptionId())
                                .map(
                                        subscription -> Subscription.builder()
                                                .id(paymentRequest.getSubscriptionId())
                                                .title(subscription.getTitle())
                                                .identityId(subscription.getIdentityId())
                                                .created(subscription.getCreated())
                                                .updated(LocalDateTime.now())
                                                .active(true)
                                                .paid(true)
                                                .isNew(false)
                                                .build()
                                )
                ).log("SUBSCRIPTION-MAPPER toSubscriptionUpdate");
    }

    public Mono<SubscriptionResponse> toSubscriptionResponse(
            Mono<Subscription> subscriptionMono, String authValue
    ) {
        return subscriptionMono
                .flatMap(
                        subscription -> webClientHandler.getIdentityById(subscription.getIdentityId(), authValue)
                                .flatMap(
                                        identityResponse -> webClientHandler.getBooksBySubscriptionId(
                                                subscription.getId(), authValue
                                        )
                                                .collectList()
                                                .flatMap(
                                                        bookResponses -> {
                                                            LocalDateTime created = subscription.getCreated();
                                                            LocalDateTime updated = subscription.getUpdated();
                                                            LocalDateTime ended = subscription.getEnded();
                                                            return Mono.just(
                                                                    SubscriptionResponse.builder()
                                                                            .id(subscription.getId())
                                                                            .title(subscription.getTitle())
                                                                            .active(subscription.getActive())
                                                                            .paid(subscription.getPaid())
                                                                            .created(created != null ? format(created) : null)
                                                                            .updated(updated != null ? format(updated) : null)
                                                                            .ended(ended != null ? format(ended) : null)
                                                                            .identityResponse(identityResponse)
                                                                            .bookResponses(bookResponses)
                                                                            .build()
                                                            );
                                                        }
                                                )
                                )
                ).log("SUBSCRIPTION-MAPPER toResponse");
    }

    private String format(LocalDateTime localDateTime) {
        return localDateTime.format(ofPattern("dd.MM.yyyy HH:mm:ss"));
    }
}
