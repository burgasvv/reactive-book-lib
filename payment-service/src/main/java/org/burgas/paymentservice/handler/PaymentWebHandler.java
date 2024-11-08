package org.burgas.paymentservice.handler;

import lombok.RequiredArgsConstructor;
import org.burgas.paymentservice.dto.PaymentRequest;
import org.burgas.paymentservice.dto.PaymentResponse;
import org.burgas.paymentservice.service.PaymentService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PaymentWebHandler {

    private final PaymentService paymentService;

    public Mono<ServerResponse> handleFindAll(@SuppressWarnings("unused") ServerRequest request) {
        return ServerResponse.ok().body(paymentService.findAll(), PaymentResponse.class);
    }

    public Mono<ServerResponse> handleFindById(ServerRequest request) {
        return ServerResponse.ok().body(
                paymentService.findById(request.pathVariable("payment-id")),
                PaymentResponse.class
        );
    }

    public Mono<ServerResponse> handleMakePayment(ServerRequest request) {
        return ServerResponse.ok().body(
                paymentService.makePayment(request.bodyToMono(PaymentRequest.class)), PaymentResponse.class
        );
    }
}
