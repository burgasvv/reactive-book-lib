package org.burgas.paymentservice.handler;

import lombok.RequiredArgsConstructor;
import org.burgas.paymentservice.dto.PaymentRequest;
import org.burgas.paymentservice.dto.PaymentResponse;
import org.burgas.paymentservice.service.PaymentService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class PaymentWebHandler {

    private final PaymentService paymentService;

    public Mono<ServerResponse> handleFindAll(ServerRequest request) {
        String authValue = request.headers().firstHeader(AUTHORIZATION);
        return ServerResponse.ok().body(paymentService.findAll(authValue), PaymentResponse.class);
    }

    public Mono<ServerResponse> handleFindById(ServerRequest request) {
        String authValue = request.headers().firstHeader(AUTHORIZATION);
        return ServerResponse.ok().body(
                paymentService.findById(request.pathVariable("payment-id"), authValue),
                PaymentResponse.class
        );
    }

    public Mono<ServerResponse> handleMakePayment(ServerRequest request) {
        String authValue = request.headers().firstHeader(AUTHORIZATION);
        return ServerResponse.ok().body(
                paymentService.makePayment(request.bodyToMono(PaymentRequest.class), authValue), PaymentResponse.class
        );
    }
}
