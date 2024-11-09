package org.burgas.subscriptionservice.handler;

import lombok.Getter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.Map;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.*;
import static org.springframework.boot.web.error.ErrorAttributeOptions.of;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.all;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Getter
@Component
public class ErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    private final ServerCodecConfigurer serverCodecConfigurer;
    private final ObjectProvider<ViewResolver> errorAttributes;

    public ErrorWebExceptionHandler(
            ErrorAttributes errorAttributes,
            WebProperties.Resources resources, ApplicationContext applicationContext,
            ServerCodecConfigurer serverCodecConfigurer, ObjectProvider<ViewResolver> viewResolvers
    ) {
        super(errorAttributes, resources, applicationContext);
        this.serverCodecConfigurer = serverCodecConfigurer;
        this.errorAttributes = viewResolvers;
        super.setMessageReaders(serverCodecConfigurer.getReaders());
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setViewResolvers(viewResolvers.stream().toList());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return route(
                all(), request -> {
                    Map<String, Object> error = errorAttributes.getErrorAttributes(
                            request, of(MESSAGE, STATUS, EXCEPTION, ERROR, STACK_TRACE, BINDING_ERRORS, PATH)
                    );
                    return ServerResponse.status(INTERNAL_SERVER_ERROR)
                            .contentType(APPLICATION_JSON)
                            .body(BodyInserters.fromValue(error));
                }
        );
    }
}
