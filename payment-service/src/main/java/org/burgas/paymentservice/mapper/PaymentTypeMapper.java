package org.burgas.paymentservice.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.paymentservice.dto.PaymentTypeRequest;
import org.burgas.paymentservice.dto.PaymentTypeResponse;
import org.burgas.paymentservice.entity.PaymentType;
import org.burgas.paymentservice.repository.PaymentTypeRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class PaymentTypeMapper {

    private final PaymentTypeRepository paymentTypeRepository;

    public Mono<PaymentType> toPaymentType(Mono<PaymentTypeRequest> paymentTypeRequestMono) {
        return paymentTypeRequestMono
                .publishOn(Schedulers.boundedElastic())
                .handle(
                        (paymentTypeRequest, paymentTypeSynchronousSink) -> {
                            try {
                                Long paymentTypeId = paymentTypeRequest.getId() == null ? 0 : paymentTypeRequest.getId();
                                paymentTypeSynchronousSink.next(
                                        PaymentType.builder()
                                                .id(paymentTypeRequest.getId())
                                                .name(paymentTypeRequest.getName())
                                                .isNew(paymentTypeRepository.findById(paymentTypeId).toFuture().get() == null)
                                                .build()
                                );
                            } catch (InterruptedException | ExecutionException e) {
                                paymentTypeSynchronousSink.error(new RuntimeException(e));
                            }
                        }
                );
    }

    public Mono<PaymentTypeResponse> toPaymentTypeResponse(Mono<PaymentType> paymentTypeMono) {
        return paymentTypeMono.map(
                paymentType -> PaymentTypeResponse.builder()
                        .id(paymentType.getId())
                        .name(paymentType.getName())
                        .build()
        );
    }
}
