package org.burgas.bookservice.handler;

import lombok.Getter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.Map;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.*;
import static org.springframework.boot.web.error.ErrorAttributeOptions.of;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Getter
@Component
public class ErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    private final ServerCodecConfigurer codecConfigurer;
    private final ObjectProvider<ViewResolver> viewResolvers;

    public ErrorWebExceptionHandler(
            ErrorAttributes errorAttributes, WebProperties.Resources resources,
            ApplicationContext applicationContext, ServerCodecConfigurer codecConfigurer,
            ObjectProvider<ViewResolver> viewResolvers
    ) {
        super(errorAttributes, resources, applicationContext);
        this.codecConfigurer = codecConfigurer;
        this.viewResolvers = viewResolvers;
        super.setMessageReaders(codecConfigurer.getReaders());
        super.setMessageWriters(codecConfigurer.getWriters());
        super.setViewResolvers(viewResolvers.stream().toList());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(
                RequestPredicates.all(), request -> {
                    Map<String, Object> error = errorAttributes.getErrorAttributes(
                            request, of(MESSAGE, EXCEPTION, ERROR, STACK_TRACE, PATH, BINDING_ERRORS, STATUS)
                    );
                    return ServerResponse.status(BAD_REQUEST)
                            .contentType(APPLICATION_JSON)
                            .body(BodyInserters.fromValue(error));
                }
        );
    }
}
