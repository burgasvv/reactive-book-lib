package org.burgas.paymentservice.router;

import lombok.RequiredArgsConstructor;
import org.burgas.paymentservice.handler.PaymentWebHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class PaymentWebRouter {

    private final PaymentWebHandler paymentWebHandler;

    @Bean
    public RouterFunction<ServerResponse> getAllPayments() {
        return RouterFunctions.route(
                RequestPredicates.GET("/payments"), paymentWebHandler::handleFindAll
        );
    }

    @Bean
    public RouterFunction<ServerResponse> getPaymentsById() {
        return RouterFunctions.route(
                RequestPredicates.GET("/payments/{payment-id}"), paymentWebHandler::handleFindById
        );
    }

    @Bean
    public RouterFunction<ServerResponse> getMakePayment() {
        return RouterFunctions.route(
                RequestPredicates.POST("/payments/make-payment"), paymentWebHandler::handleMakePayment
        );
    }
}
