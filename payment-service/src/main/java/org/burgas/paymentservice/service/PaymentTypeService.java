package org.burgas.paymentservice.service;

import lombok.RequiredArgsConstructor;
import org.burgas.paymentservice.dto.PaymentTypeRequest;
import org.burgas.paymentservice.dto.PaymentTypeResponse;
import org.burgas.paymentservice.handler.WebClientHandler;
import org.burgas.paymentservice.mapper.PaymentTypeMapper;
import org.burgas.paymentservice.repository.PaymentTypeRepository;
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
public class PaymentTypeService {

    private final PaymentTypeRepository paymentTypeRepository;
    private final PaymentTypeMapper paymentTypeMapper;
    private final WebClientHandler webClientHandler;

    public Flux<PaymentTypeResponse> findAll() {
        return paymentTypeRepository.findAll()
                .flatMap(
                        paymentType -> paymentTypeMapper.toPaymentTypeResponse(Mono.just(paymentType))
                );
    }

    public Mono<PaymentTypeResponse> findById(String paymentTypeId) {
        return paymentTypeRepository.findById(Long.valueOf(paymentTypeId))
                .flatMap(paymentType -> paymentTypeMapper.toPaymentTypeResponse(Mono.just(paymentType)));
    }

    @Transactional(
            isolation = SERIALIZABLE,
            propagation = REQUIRED,
            rollbackFor = Exception.class
    )
    public Mono<PaymentTypeResponse> createOrUpdate(Mono<PaymentTypeRequest> paymentTypeRequestMono, String authValue) {
        return paymentTypeRequestMono.flatMap(
                paymentTypeRequest -> webClientHandler.getPrincipal(authValue)
                        .flatMap(
                                identityPrincipal -> {
                                    if (
                                            identityPrincipal.getIsAuthenticated() &&
                                            Objects.equals(identityPrincipal.getAuthorities().getFirst(), "ADMIN")
                                    ) {
                                        return paymentTypeMapper.toPaymentType(Mono.just(paymentTypeRequest))
                                                .flatMap(paymentTypeRepository::save)
                                                .flatMap(
                                                        paymentType -> paymentTypeMapper
                                                                .toPaymentTypeResponse(Mono.just(paymentType))
                                                );
                                    } else
                                        return Mono.error(
                                                new RuntimeException("Пользователь не авторизован и не имеет прав на действие")
                                        );
                                }
                        )
        );
    }
}
