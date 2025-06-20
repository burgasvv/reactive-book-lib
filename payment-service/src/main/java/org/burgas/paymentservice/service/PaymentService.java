package org.burgas.paymentservice.service;

import lombok.RequiredArgsConstructor;
import org.burgas.paymentservice.dto.PaymentRequest;
import org.burgas.paymentservice.dto.PaymentResponse;
import org.burgas.paymentservice.dto.SubscriptionResponse;
import org.burgas.paymentservice.entity.Payment;
import org.burgas.paymentservice.handler.WebClientHandler;
import org.burgas.paymentservice.mapper.PaymentMapper;
import org.burgas.paymentservice.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static org.springframework.transaction.annotation.Isolation.REPEATABLE_READ;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final WebClientHandler webClientHandler;

    public Flux<PaymentResponse> findAll(String authValue) {
        return paymentRepository.findAll()
                .flatMap(
                        payment -> paymentMapper.toPaymentResponse(Mono.just(payment), authValue)
                );
    }

    public Mono<PaymentResponse> findById(String paymentId, String authValue) {
        return paymentRepository.findById(Long.valueOf(paymentId))
                .flatMap(
                        payment -> paymentMapper.toPaymentResponse(Mono.just(payment), authValue)
                );
    }

    @Transactional(
            isolation = REPEATABLE_READ,
            propagation = REQUIRED,
            rollbackFor = Exception.class
    )
    public Mono<PaymentResponse> makePayment(Mono<PaymentRequest> paymentRequestMono, String authValue) {
        return paymentRequestMono
                .flatMap(
                        paymentRequest -> webClientHandler.getPrincipal(authValue)
                                .flatMap(
                                        identityPrincipal -> {
                                            if (
                                                    identityPrincipal.getIsAuthenticated() &&
                                                    Objects.equals(identityPrincipal.getId(), paymentRequest.getIdentityId())
                                            ) {
                                                Mono<Payment> savedPayment = paymentMapper.toPayment(Mono.just(paymentRequest))
                                                        .flatMap(paymentRepository::save);
                                                Mono<SubscriptionResponse> subResp = webClientHandler
                                                        .updateSubscriptionAfterPayment(Mono.just(paymentRequest), authValue);

                                                return paymentMapper.toPaymentResponse(savedPayment, authValue)
                                                        .flatMap(
                                                                paymentResponse -> subResp.flatMap(
                                                                        subscriptionResponse ->
                                                                        {
                                                                            paymentResponse.setSubscriptionResponse(subscriptionResponse);
                                                                            return Mono.just(paymentResponse);
                                                                        }
                                                                )
                                                        );
                                            } else
                                                return Mono.error(
                                                        new RuntimeException("Пользователь не авторизован и не имеет прав доступа")
                                                );
                                        }
                                )
                );
    }
}
