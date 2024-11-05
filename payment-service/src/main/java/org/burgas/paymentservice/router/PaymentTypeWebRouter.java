package org.burgas.paymentservice.router;

import lombok.RequiredArgsConstructor;
import org.burgas.paymentservice.handler.PaymentTypeWebHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class PaymentTypeWebRouter {

    private final PaymentTypeWebHandler paymentTypeWebHandler;

    @Bean
    public RouterFunction<ServerResponse> paymentTypeRouter() {
        return RouterFunctions.route()
                .GET("/payment-types", paymentTypeWebHandler::handleFindAll)
                .GET("/payment-types/{paymentType-id}", paymentTypeWebHandler::handleFindById)
                .POST("/payment-types/create", paymentTypeWebHandler::handleCreateOrUpdate)
                .PUT("/payment-types/edit", paymentTypeWebHandler::handleCreateOrUpdate)
                .build();
    }
}
