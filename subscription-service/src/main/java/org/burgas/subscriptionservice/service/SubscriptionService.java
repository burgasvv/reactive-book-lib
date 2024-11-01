package org.burgas.subscriptionservice.service;

import lombok.RequiredArgsConstructor;
import org.burgas.subscriptionservice.dto.SubscriptionRequest;
import org.burgas.subscriptionservice.dto.SubscriptionResponse;
import org.burgas.subscriptionservice.entity.Subscription;
import org.burgas.subscriptionservice.handler.WebClientHandler;
import org.burgas.subscriptionservice.mapper.SubscriptionMapper;
import org.burgas.subscriptionservice.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final WebClientHandler webClientHandler;

    public Flux<SubscriptionResponse> findByIdentityId(String identityId, String authorizeValue) {
        return subscriptionRepository.findSubscriptionsByIdentityId(Long.valueOf(identityId))
                .map(subscription -> subscriptionMapper.toSubscriptionResponse(subscription, authorizeValue));
    }

    public Mono<SubscriptionResponse> findById(String subscriptionId, String authorizeValue) {
        return subscriptionRepository.findById(Long.valueOf(subscriptionId))
                .map(subscription -> subscriptionMapper.toSubscriptionResponse(subscription, authorizeValue));
    }

    @Transactional(
            isolation = SERIALIZABLE,
            propagation = REQUIRED,
            rollbackFor = Exception.class
    )
    public Mono<SubscriptionResponse> create(Mono<SubscriptionRequest> subscriptionRequestMono, String authorizeValue) {
        return subscriptionRequestMono.flatMap(
                subscriptionRequest -> webClientHandler.getPrincipal(authorizeValue)
                        .flatMap(
                                identityPrincipal -> {
                                    if (identityPrincipal.getIsAuthenticated() &&
                                        Objects.equals(
                                                identityPrincipal.getId(), subscriptionRequest.getIdentityId())
                                    ) {
                                        Subscription create = subscriptionMapper.toSubscriptionCreate(subscriptionRequest);
                                        return subscriptionRepository.save(create)
                                                .map(
                                                        subscription -> subscriptionMapper
                                                                .toSubscriptionResponse(subscription, authorizeValue)
                                                );

                                    } else
                                        return Mono.error(
                                                new RuntimeException(
                                                        "Пользователь не авторизован " +
                                                        "или пытается создать абонемент с чужого аккаунта")
                                        );
                                }
                        )
        );
    }

    @Transactional(
            isolation = SERIALIZABLE,
            propagation = REQUIRED,
            rollbackFor = Exception.class
    )
    public Mono<SubscriptionResponse> update(Mono<SubscriptionRequest> subscriptionRequestMono, String authorizeValue) {
        return subscriptionRequestMono.flatMap(
                subscriptionRequest -> webClientHandler.getPrincipal(authorizeValue)
                        .flatMap(
                                identityPrincipal -> {
                                    if (identityPrincipal.getIsAuthenticated() &&
                                        Objects.equals(
                                                identityPrincipal.getId(), subscriptionRequest.getIdentityId())
                                    ) {
                                        Subscription create = subscriptionMapper.toSubscriptionUpdate(subscriptionRequest);
                                        return subscriptionRepository.save(create)
                                                .map(
                                                        subscription -> subscriptionMapper
                                                                .toSubscriptionResponse(subscription, authorizeValue)
                                                );

                                    } else
                                        return Mono.error(
                                                new RuntimeException(
                                                        "Пользователь не авторизован " +
                                                        "или пытается создать абонемент с чужого аккаунта")
                                        );
                                }
                        )
        );
    }
}
