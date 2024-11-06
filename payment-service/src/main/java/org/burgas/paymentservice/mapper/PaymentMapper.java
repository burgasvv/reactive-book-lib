package org.burgas.paymentservice.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.paymentservice.dto.PaymentRequest;
import org.burgas.paymentservice.dto.PaymentResponse;
import org.burgas.paymentservice.entity.Payment;
import org.burgas.paymentservice.handler.WebClientHandler;
import org.burgas.paymentservice.repository.PaymentTypeRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class PaymentMapper {

    private final PaymentTypeRepository paymentTypeRepository;
    private final PaymentTypeMapper paymentTypeMapper;
    private final WebClientHandler webClientHandler;

    public Mono<Payment> toPayment(Mono<PaymentRequest> paymentRequestMono) {
        return paymentRequestMono
                .map(
                        paymentRequest ->
                            Payment.builder()
                                    .id(paymentRequest.getId() == null ? 0L : paymentRequest.getId())
                                    .paymentTypeId(paymentRequest.getPaymentTypeId())
                                    .subscriptionId(paymentRequest.getSubscriptionId())
                                    .isNew(true)
                                    .build()
                );
    }

    public Mono<PaymentResponse> toPaymentResponse(Mono<Payment> paymentMono, String authValue) {
        return paymentMono
                .publishOn(Schedulers.boundedElastic())
                .handle(
                        (payment, paymentResponseSynchronousSink) ->
                        {
                            try {
                                paymentResponseSynchronousSink.next(
                                        PaymentResponse.builder()
                                                .id(payment.getId())
                                                .paymentTypeResponse(
                                                        paymentTypeMapper.toPaymentTypeResponse(
                                                                paymentTypeRepository.findById(payment.getPaymentTypeId())
                                                        )
                                                                .toFuture().get()
                                                ).subscriptionResponse(
                                                        webClientHandler.getSubscriptionById(payment.getSubscriptionId(), authValue)
                                                                .toFuture().get()
                                                )
                                                .build()
                                );
                            } catch (InterruptedException | ExecutionException e) {
                                paymentResponseSynchronousSink.error(new RuntimeException(e));
                            }
                        }
                );
    }
}
