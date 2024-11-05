package org.burgas.paymentservice.handler;

import lombok.RequiredArgsConstructor;
import org.burgas.paymentservice.dto.PaymentTypeRequest;
import org.burgas.paymentservice.dto.PaymentTypeResponse;
import org.burgas.paymentservice.service.PaymentTypeService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class PaymentTypeWebHandler {

    private final PaymentTypeService paymentTypeService;

    public Mono<ServerResponse> handleFindAll(@SuppressWarnings("unused") final ServerRequest request) {
        return ServerResponse.ok().body(paymentTypeService.findAll(), PaymentTypeResponse.class);
    }

    public Mono<ServerResponse> handleFindById(final ServerRequest request) {
        return ServerResponse.ok()
                .body(paymentTypeService.findById(request.pathVariable("paymentType-id")),
                        PaymentTypeResponse.class);
    }

    public Mono<ServerResponse> handleCreateOrUpdate(final ServerRequest request) {
        String authValue = request.headers().firstHeader(AUTHORIZATION);
        return ServerResponse.ok()
                .body(
                        paymentTypeService.createOrUpdate(request.bodyToMono(PaymentTypeRequest.class), authValue),
                        PaymentTypeResponse.class
                );
    }
}
