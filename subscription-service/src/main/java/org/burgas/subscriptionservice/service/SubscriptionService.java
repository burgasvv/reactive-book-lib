package org.burgas.subscriptionservice.service;

import lombok.RequiredArgsConstructor;
import org.burgas.subscriptionservice.dto.IdentityPrincipal;
import org.burgas.subscriptionservice.dto.PaymentRequest;
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

    public Flux<SubscriptionResponse> findByIdentityId(String identityId, String authValue) {
        return Flux.zip(
                subscriptionRepository.findSubscriptionsByIdentityId(Long.valueOf(identityId)),
                webClientHandler.getPrincipal(authValue)
        )
                .flatMap(
                        objects -> {
                            Subscription subscription = objects.getT1();
                            IdentityPrincipal identity = objects.getT2();
                            if (
                                    identity.getIsAuthenticated() &&
                                    (Objects.equals(identity.getId(), subscription.getIdentityId()) ||
                                     Objects.equals(identity.getAuthorities().getFirst(), "ADMIN"))
                            ) {
                                return subscriptionMapper.toSubscriptionResponse(Mono.just(subscription), authValue);
                            } else
                                return Mono.error(
                                        new RuntimeException("Пользователь не авторизован и не имеет прав доступа")
                                );
                        }
                );
    }

    public Mono<SubscriptionResponse> findById(String subscriptionId, String authValue) {
        return Mono.zip(
                subscriptionRepository.findById(Long.valueOf(subscriptionId)),
                webClientHandler.getPrincipal(authValue)
        )
                .flatMap(
                        objects -> {
                            Subscription subscription = objects.getT1();
                            IdentityPrincipal principal = objects.getT2();
                            if (
                                    principal.getIsAuthenticated() &&
                                    (Objects.equals(principal.getId(), subscription.getIdentityId()) ||
                                     Objects.equals(principal.getAuthorities().getFirst(), "ADMIN"))
                            ) {
                                return subscriptionMapper.toSubscriptionResponse(Mono.just(subscription), authValue);
                            } else
                                return Mono.error(
                                        new RuntimeException("Пользователь не авторизован и не имеет прав доступа")
                                );
                        }
                )
                .log("SUBSCRIPTION-SERVICE");
    }

    @Transactional(
            isolation = SERIALIZABLE,
            propagation = REQUIRED,
            rollbackFor = Exception.class
    )
    public Mono<SubscriptionResponse> create(Mono<SubscriptionRequest> subscriptionRequestMono, String authValue) {
        return subscriptionRequestMono.flatMap(
                subscriptionRequest -> webClientHandler.getPrincipal(authValue)
                        .flatMap(
                                identityPrincipal -> {
                                    if (identityPrincipal.getIsAuthenticated() &&
                                        Objects.equals(
                                                identityPrincipal.getId(), subscriptionRequest.getIdentityId())
                                    ) {
                                        return subscriptionMapper.toSubscriptionCreate(Mono.just(subscriptionRequest))
                                                .flatMap(subscriptionRepository::save)
                                                .flatMap(
                                                        subscription -> subscriptionMapper
                                                                .toSubscriptionResponse(Mono.just(subscription), authValue)
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
    public Mono<SubscriptionResponse> updateAfterPayment(Mono<PaymentRequest> paymentRequestMono, String authValue) {
        return paymentRequestMono.flatMap(
                paymentRequest -> webClientHandler.getPrincipal(authValue)
                        .flatMap(
                                identityPrincipal -> {
                                    if (identityPrincipal.getIsAuthenticated() &&
                                        Objects.equals(
                                                identityPrincipal.getId(), paymentRequest.getIdentityId())
                                    ) {
                                        return subscriptionMapper.toSubscriptionUpdate(Mono.just(paymentRequest))
                                                .flatMap(subscriptionRepository::save)
                                                .flatMap(
                                                        subscription -> subscriptionMapper
                                                                .toSubscriptionResponse(Mono.just(subscription), authValue)
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
    public Mono<String> addBookToSubscription(
            Mono<SubscriptionRequest> requestMono, String bookId, String authValue
    ) {

        return Mono.zip(requestMono, webClientHandler.getPrincipal(authValue))
                .flatMap(
                        objects -> {
                            SubscriptionRequest subscriptionRequest = objects.getT1();
                            IdentityPrincipal identityPrincipal = objects.getT2();
                            return subscriptionRepository.findById(subscriptionRequest.getId())
                                    .flatMap(
                                            subscription -> {
                                                if (
                                                        identityPrincipal.getIsAuthenticated() &&
                                                        Objects.equals(identityPrincipal.getId(), subscriptionRequest.getIdentityId()) &&
                                                        subscription.getActive() && subscription.getPaid()
                                                ) {
                                                    return subscriptionRepository
                                                            .addBookToSubscription(subscriptionRequest.getId(), Long.valueOf(bookId))
                                                            .then(
                                                                    Mono.fromCallable(() -> "Книга успешно добавлена в абонемент")
                                                            );
                                                } else
                                                    return Mono.error(
                                                            new RuntimeException("Пользователь не авторизован")
                                                    );
                                            }
                                    );
                        }
                );
    }
}
